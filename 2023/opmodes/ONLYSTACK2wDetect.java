/*
Copyright (c) September 2017 FTC Teams 25/5218
All rights reserved.
Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:
Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.
Neither the name of FTC Teams 25/5218 nor the names of their contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.
NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package opmodes;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.openftc.apriltag.AprilTagDetection;

import team25core.DeadReckonPath;
import team25core.DeadReckonTask;
import team25core.DistanceSensorCriteria;
import team25core.FourWheelDirectDrivetrain;
import team25core.OneWheelDirectDrivetrain;
import team25core.Robot;
import team25core.RobotEvent;
import team25core.RunToEncoderValueTask;
import team25core.SingleShotTimerTask;
import team25core.vision.apriltags.AprilTagDetectionTask;


@Autonomous(name = "ONLYDRIVESTACK2")
//@Disabled
public class ONLYSTACK2wDetect extends Robot {


    //wheels
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private FourWheelDirectDrivetrain drivetrain;


    //mechs
    private Servo umbrella;

    private DcMotor linearLift;
    private OneWheelDirectDrivetrain liftDriveTrain;

    private DcMotor turret;
    private OneWheelDirectDrivetrain turretDrivetrain;


    //sensors
    private DistanceSensor alignerDistanceSensor;
    private DistanceSensorCriteria distanceSensorCriteria;
    private ColorSensor linearColorSensor;


    //paths
    private DeadReckonPath goDropPreLoadPath;
    private DeadReckonPath goToJunctionPath;
    private DeadReckonPath goToStackPath;
    private DeadReckonPath goStrafeToJunction;

    private DeadReckonPath goParkPath;
    private DeadReckonPath goParkPathLeft;
    private DeadReckonPath goParkPathMiddle;
    private DeadReckonPath goParkPathRight;


    private DeadReckonPath liftMech;
    private DeadReckonPath  lowerMech;
    private DeadReckonPath  lowerMech2;


    private DeadReckonPath strafeOutPath;

    private DeadReckonPath deliverConePath;

    //variables for constants
    static final double FORWARD_DISTANCE = 13.5;
    static final double DRIVE_SPEED = 0.4;

    // apriltags detection
    private Telemetry.Item tagIdTlm;
    private Telemetry.Item parkingLocationTlm;
    AprilTagDetection tagObject;
    private AprilTagDetectionTask detectionTask;

    //telemetry
    private Telemetry.Item whereAmI;

    private RunToEncoderValueTask linearLiftTask;

    private RunToEncoderValueTask linearLiftTaskJunction;

    private RunToEncoderValueTask linearLiftTaskStack;

    private static final int DELAY = 5000;

    private String parkpath = "";
    private DeadReckonPath parkPath1;

    private ColorSensor bottomColorsensor;

    /*
     * The default event handler for the robot.
     */

    @Override
    public void handleEvent(RobotEvent e)
    {
        /*
         * Every time we complete a segment drop a note in the robot log.
         */
        if (e instanceof DeadReckonTask.DeadReckonEvent) {
            RobotLog.i("Completed path segment %d", ((DeadReckonTask.DeadReckonEvent)e).segment_num);
        }
    }





    public void initPaths()
    {
        goToJunctionPath = new DeadReckonPath();
        goToStackPath = new DeadReckonPath();
        goStrafeToJunction= new DeadReckonPath();

        strafeOutPath = new DeadReckonPath();

        goParkPathRight  = new DeadReckonPath();
        goParkPathRight.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 2, DRIVE_SPEED);
        goParkPathRight.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 8, 0.3);
        goParkPathRight.addSegment(DeadReckonPath.SegmentType.STRAIGHT, FORWARD_DISTANCE , -0.25);


        goParkPathMiddle  = new DeadReckonPath();
        goParkPathMiddle.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 2, DRIVE_SPEED);

        goParkPathLeft  = new DeadReckonPath();
        goParkPathLeft.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 2, DRIVE_SPEED);
        goParkPathLeft.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 6, DRIVE_SPEED);
        goParkPathLeft.addSegment(DeadReckonPath.SegmentType.STRAIGHT, FORWARD_DISTANCE + 1, 0.25);

        goToJunctionPath.stop();
        goToStackPath.stop();
        goStrafeToJunction.stop();

        strafeOutPath.stop();

        liftMech = new DeadReckonPath();
        liftMech.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 10, -0.5);

        lowerMech =  new DeadReckonPath();
        lowerMech.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 15, 0.5);

        lowerMech2 =  new DeadReckonPath();
        lowerMech2.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 10, 0.5);
        deliverConePath  = new DeadReckonPath();
        deliverConePath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 5.5,  -DRIVE_SPEED);


        goDropPreLoadPath= new DeadReckonPath();
        goDropPreLoadPath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 25, DRIVE_SPEED);
        goDropPreLoadPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 1.5, -0.3);

        //drive path 1
        goToStackPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 2, 0.3);
        goToStackPath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 10, DRIVE_SPEED);
        goToStackPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, FORWARD_DISTANCE + 1, -0.25);
        //aaa hi
        //drive path 2
        goToJunctionPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, FORWARD_DISTANCE + 7, DRIVE_SPEED);

        //drive path 3
        goStrafeToJunction.addSegment(DeadReckonPath.SegmentType.SIDEWAYS,1.75,-0.25);

        linearLiftTask = new RunToEncoderValueTask(this,linearLift,2800,-0.8);
        linearLiftTaskJunction = new RunToEncoderValueTask(this,linearLift,3100,-0.5);
        linearLiftTaskStack = new RunToEncoderValueTask(this,linearLift,3300,0.5);

        strafeOutPath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS,2,0.25);
        strafeOutPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 23, -0.25);




    }

    @Override
    public void init()
    {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        umbrella=hardwareMap.servo.get("umbrella");



        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        drivetrain = new FourWheelDirectDrivetrain(frontRight, backRight, frontLeft, backLeft);
        drivetrain.resetEncoders();
        drivetrain.encodersOn();

        whereAmI = telemetry.addData("location in code", "init");
        tagIdTlm = telemetry.addData("tagId","none");
        parkingLocationTlm = telemetry.addData("parking location: ","none");

        linearLift=hardwareMap.get(DcMotor.class, "linearLift");
        linearLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        liftDriveTrain = new OneWheelDirectDrivetrain(linearLift);
        liftDriveTrain.resetEncoders();
        liftDriveTrain.encodersOn();

        turret = hardwareMap.get(DcMotor.class, "turret");
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        turretDrivetrain = new OneWheelDirectDrivetrain(turret);
        turretDrivetrain.resetEncoders();
        turretDrivetrain.encodersOn();

        //open umbrella & lock cone
        //umbrella.setPosition(0.55);
        umbrella.setPosition(0.55);


        linearColorSensor = hardwareMap.get(RevColorSensorV3.class, "liftColorSensor");
        alignerDistanceSensor = hardwareMap.get(Rev2mDistanceSensor.class, "alignerDistanceSensor");

        turret.setTargetPosition(0);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setPower(0.5);


        initPaths();


    }



    public void goDropPreLoad()
    {
        parkingLocationTlm.setValue("went to middle target zone");

        this.addTask(new DeadReckonTask(this, goDropPreLoadPath,drivetrain ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    RobotLog.i("went to middle target zone");
                    whereAmI.setValue("went to middle target zone");
                    delayAndDrop0(2000);


                }
            }
        });
    }

    private void delayAndDrop0(int delayInMsec) {
        this.addTask(new SingleShotTimerTask(this, delayInMsec) {
            @Override
            public void handleEvent(RobotEvent e) {
                SingleShotTimerEvent event = (SingleShotTimerEvent) e;
                if (event.kind == EventKind.EXPIRED ) {
                    whereAmI.setValue("in delay task");
                    dropCone0();

                }
            }
        });

    }

    private void dropCone0() {
        umbrella.setPosition(0);
        delayAndDrop1(3000);
    }

    private void delayAndDrop1(int delayInMsec) {
        this.addTask(new SingleShotTimerTask(this, delayInMsec) {
            @Override
            public void handleEvent(RobotEvent e) {
                SingleShotTimerEvent event = (SingleShotTimerEvent) e;
                if (event.kind == EventKind.EXPIRED ) {
                    whereAmI.setValue("in delay task");

                    if (parkpath == "right") {
                        goPark(goParkPathRight);
                    }
                    if (parkpath == "left") {
                        goPark(goParkPathLeft);

                    }
                    if (parkpath == "middle") {
                        goPark(goParkPathMiddle);

                    }


                }
            }
        });

    }


    public void goPark(DeadReckonPath path)
    {
        parkingLocationTlm.setValue("went to middle target zone");

        this.addTask(new DeadReckonTask(this, path,drivetrain ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    RobotLog.i("went to middle target zone");
                    whereAmI.setValue("went to middle target zone");


                }
            }
        });
    }

    @Override
    public void start()
    {
        whereAmI.setValue("in Start");

        setAprilTagDetection();
        addTask(detectionTask);

//        goDropPreLoad();
//        addTask(linearLiftTask);



    }

    public void setAprilTagDetection()
    {
        detectionTask = new AprilTagDetectionTask(this, "Webcam 1") {
            @Override
            public void handleEvent(RobotEvent e) {
                TagDetectionEvent event = (TagDetectionEvent) e;
                tagObject = event.tagObject;
                tagIdTlm.setValue(tagObject.id);
                whereAmI.setValue("in handleEvent");

                switch (tagObject.id) {
                    case 0:
                        parkpath = "left";
                        addTask(linearLiftTask);
                        goDropPreLoad();
                        break;
                    case 6:
                        parkpath = "right";
                        addTask(linearLiftTask);
                        goDropPreLoad();
                        break;
                    case 19:
                        parkpath = "middle";
                        addTask(linearLiftTask);
                        goDropPreLoad();
                        break;
                }
            }
        };

        whereAmI.setValue("setAprilTagDetection");
        detectionTask.init(telemetry, hardwareMap);
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void gotoStack()
    {
        parkingLocationTlm.setValue("went to middle target zone");

        this.addTask(new DeadReckonTask(this, goToStackPath,drivetrain ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    RobotLog.i("went to middle target zone");
                    whereAmI.setValue("went to middle target zone");
                    delayAndDrop(2000);


                }
            }
        });
    }

    private void delayAndDrop(int delayInMsec) {
        this.addTask(new SingleShotTimerTask(this, delayInMsec) {
            @Override
            public void handleEvent(RobotEvent e) {
                SingleShotTimerEvent event = (SingleShotTimerEvent) e;
                if (event.kind == EventKind.EXPIRED ) {
                    whereAmI.setValue("in delay task");
                    golowerMech();

                }
            }
        });

    }


    private void golowerMech() {
        this.addTask(new DeadReckonTask(this, lowerMech, liftDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    whereAmI.setValue("lifted linear lift");
                    grabcone();

                }
            }
        });
    }

    private void grabcone() {
        umbrella.setPosition(0.55);
        whereAmI.setValue("grabbed the cone");
        goliftMech();
    }


    private void goliftMech() {
        this.addTask(new DeadReckonTask(this, liftMech, liftDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    whereAmI.setValue("lifted linear lift");
                    goToJunction();
                    addTask(linearLiftTaskJunction);



                }
            }
        });
    }

    public void goToJunction()
    {
        parkingLocationTlm.setValue("went to middle target zone");

        this.addTask(new DeadReckonTask(this, goToJunctionPath,drivetrain ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    RobotLog.i("went to middle target zone");
                    whereAmI.setValue("went to middle target zone");
                    goTurnTurret();



                }
            }
        });
    }

    public void goTurnTurret()
    {
        turret.setTargetPosition(-800);
        turret.setPower(0.5);
        goStrafeToJunction();

    }


    public void goStrafeToJunction()
    {
        parkingLocationTlm.setValue("went to middle target zone");

        this.addTask(new DeadReckonTask(this, goStrafeToJunction,drivetrain ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    RobotLog.i("went to middle target zone");
                    whereAmI.setValue("went to middle target zone");
                    delayAndDrop2(1300);

                }
            }
        });
    }



    private void delayAndDrop2(int delayInMsec) {
        this.addTask(new SingleShotTimerTask(this, delayInMsec) {
            @Override
            public void handleEvent(RobotEvent e) {
                SingleShotTimerEvent event = (SingleShotTimerEvent) e;
                if (event.kind == EventKind.EXPIRED ) {
                    whereAmI.setValue("in delay task");
                    dropCone();
                }
            }
        });

    }

    private void dropCone() {
        umbrella.setPosition(0);
        whereAmI.setValue("dropped the cone");

        delayAndDrop3(1300);



    }

    private void delayAndDrop3(int delayInMsec) {
        this.addTask(new SingleShotTimerTask(this, delayInMsec) {
            @Override
            public void handleEvent(RobotEvent e) {
                SingleShotTimerEvent event = (SingleShotTimerEvent) e;
                if (event.kind == EventKind.EXPIRED ) {
                    whereAmI.setValue("in delay task");
                    goTurnTurretOG();

                }
            }
        });

    }

    public void goTurnTurretOG()
    {
        turret.setTargetPosition(0);
        turret.setPower(0.5);
        //goToStackAgain();
        //addTask(linearLiftTaskStack);

    }




    public void goToStackAgain()
    {
        parkingLocationTlm.setValue("went to middle target zone");

        this.addTask(new DeadReckonTask(this, strafeOutPath,drivetrain ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    RobotLog.i("went to middle target zone");
                    whereAmI.setValue("went to middle target zone");
                    delayAndDrop4(2000);


                }
            }
        });
    }

    private void delayAndDrop4(int delayInMsec) {
        this.addTask(new SingleShotTimerTask(this, delayInMsec) {
            @Override
            public void handleEvent(RobotEvent e) {
                SingleShotTimerEvent event = (SingleShotTimerEvent) e;
                if (event.kind == EventKind.EXPIRED ) {
                    whereAmI.setValue("in delay task");
                    golowerMech2();

                }
            }
        });

    }

    private void golowerMech2() {
        this.addTask(new DeadReckonTask(this, lowerMech2, liftDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    whereAmI.setValue("lifted linear lift");
                    grabcone();


                }
            }
        });
    }







}