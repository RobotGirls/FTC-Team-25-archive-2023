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
package opmodes.workingcode;

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


@Autonomous(name = "PreLoadLeft")
//@Disabled

// PROGRAM THAT WILL ONLY DROP PRELOADED ON RED-LEFT SIDE WITH ONEWHEELDRIVTRAIN
public class PreLoadLeftRed2 extends Robot {


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
    private DeadReckonPath leftPath;
    private DeadReckonPath middlePath;
    private DeadReckonPath rightPath;


    private DeadReckonPath liftMech;
    private DeadReckonPath  lowerMech;

    private DeadReckonTask linearTask;

    private DeadReckonPath turretTurnOrangePath;
    private DeadReckonPath turretTurnBluePath;

    private DeadReckonPath goToJunctionPath;

    private DeadReckonPath deliverConePath;

    //variables for constants
    static final double FORWARD_DISTANCE = 13.5;
    static final double DRIVE_SPEED = 0.4;

    // apriltags detection
    private Telemetry.Item tagIdTlm;
    private Telemetry.Item parkingLocationTlm;
    AprilTagDetection tagObject;
    private AprilTagDetectionTask detectionTask;

    public static double EDITTHIS = 5;


    //telemetry
    private Telemetry.Item whereAmI;

    private RunToEncoderValueTask linearLiftTask;

    private static final int DELAY = 5000;
    private String detect = "";

    public String delaySequence = "will drop cone";

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



    public void setAprilTagDetection() {
        detectionTask = new AprilTagDetectionTask(this, "Webcam 1") {
            @Override
            public void handleEvent(RobotEvent e) {
                TagDetectionEvent event = (TagDetectionEvent) e;
                tagObject = event.tagObject;
                tagIdTlm.setValue(tagObject.id);
                whereAmI.setValue("in handleEvent");

                if (tagObject.id == 0) {
                    detect = "left";
                    lift();
                    addTask(linearTask);
                    goToJunction();

                }
                if (tagObject.id == 6) {
                    detect = "right";
                    lift();
                    addTask(linearTask);
                    goToJunction();
                }
                if (tagObject.id == 19) {
                    detect = "middle";
                    lift();
                    addTask(linearTask);
                    goToJunction();
                }
               //addTask(linearLiftTask);




            }
        };
        whereAmI.setValue("setAprilTagDetection");
        detectionTask.init(telemetry, hardwareMap);
    }



    public void initPaths()
    {
        leftPath = new DeadReckonPath();
        middlePath = new DeadReckonPath();
        rightPath= new DeadReckonPath();

        goToJunctionPath = new DeadReckonPath();


        leftPath.stop();
        middlePath.stop();
        rightPath.stop();

        goToJunctionPath.stop();

        liftMech = new DeadReckonPath();
        liftMech.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 41, 0.8);

        lowerMech =  new DeadReckonPath();
        lowerMech.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 3, -0.8);

        deliverConePath  = new DeadReckonPath();
        deliverConePath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 5.5,  -DRIVE_SPEED);

        //going forward then to the left
        leftPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 1, DRIVE_SPEED);
        leftPath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 23, DRIVE_SPEED);


        //going forward
        middlePath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 8, DRIVE_SPEED);
//        middlePath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 8, -DRIVE_SPEED); ///og37
        // middlePath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 3, 0.2);

        //going forward then right
        rightPath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS,8,-DRIVE_SPEED);


       // goToJunctionPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, FORWARD_DISTANCE + 1, -DRIVE_SPEED);
        goToJunctionPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, FORWARD_DISTANCE +6, -DRIVE_SPEED);
        goToJunctionPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT,EDITTHIS , DRIVE_SPEED);
        goToJunctionPath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 8, -0.3);
        goToJunctionPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 2, -0.3);




//5300
        linearLiftTask = new RunToEncoderValueTask(this,linearLift,3000,0.4);


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

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        drivetrain = new FourWheelDirectDrivetrain(frontRight, backRight, frontLeft, backLeft);
        drivetrain.resetEncoders();
        drivetrain.encodersOn();

        whereAmI = telemetry.addData("location in code", "init");
        tagIdTlm = telemetry.addData("tagId","none");
        parkingLocationTlm = telemetry.addData("parking location: ","none");

        linearLift=hardwareMap.get(DcMotor.class, "linearLift");
        linearLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        linearLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

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
        umbrella.setPosition(0.55);


        linearColorSensor = hardwareMap.get(RevColorSensorV3.class, "liftColorSensor");
        alignerDistanceSensor = hardwareMap.get(Rev2mDistanceSensor.class, "alignerDistanceSensor");

        turret.setTargetPosition(0);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setPower(0.5);


        initPaths();


    }




    //  lifting & dropping paths --------------------------------------

    public void  goToJunction()
    {

        this.addTask(new DeadReckonTask(this, goToJunctionPath,drivetrain ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {

                    delayAndDrop(3000);




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

                    dropCone();

                }
            }
        });

    }
    private void dropCone() {
        umbrella.setPosition(0);
        delayAndDrop2(1000);
        whereAmI.setValue("dropped the cone");


    }

    private void delayAndDrop2(int delayInMsec) {
        this.addTask(new SingleShotTimerTask(this, delayInMsec) {
            @Override
            public void handleEvent(RobotEvent e) {
                SingleShotTimerEvent event = (SingleShotTimerEvent) e;
                if (event.kind == EventKind.EXPIRED ) {
                    whereAmI.setValue("in delay task");
                    detectedBasedPathSelection();





                }
            }
        });

    }



    private void detectedBasedPathSelection(){



        if ( detect == "right")
        {
            gotoRightPark();
        }
        if ( detect == "middle")
        {
            gotoMiddlePark();
        }
        if ( detect == "left")
        {
            gotoLeftPark();
        }

    }




    // parking paths -----------------------------------

    public void gotoRightPark()
    {

        parkingLocationTlm.setValue("went to right target zone");

        this.addTask(new DeadReckonTask(this, rightPath,drivetrain ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    RobotLog.i("went to right target zone");
                    whereAmI.setValue("went to right target zone");

                }
            }
        });
    }

    public void gotoMiddlePark()
    {
        parkingLocationTlm.setValue("went to middle target zone");

        this.addTask(new DeadReckonTask(this, middlePath,drivetrain ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    RobotLog.i("went to middle target zone");
                    whereAmI.setValue("went to middle target zone");
                    delayAndDrop(DELAY);


                }
            }
        });
    }



    public void gotoLeftPark()
    {


        parkingLocationTlm.setValue("went to left target zone");


        this.addTask(new DeadReckonTask(this, leftPath,drivetrain ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    RobotLog.i("went to left target zone");
                    whereAmI.setValue("went to left target zone");

                }
            }
        });


    }

    public void lift()
    {
        linearTask = new DeadReckonTask(this, liftMech,liftDriveTrain ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    whereAmI.setValue("lifting lift");

                }
            }
        };
    }


    @Override
    public void start()
    {
        whereAmI.setValue("in Start");
        setAprilTagDetection();
        addTask(detectionTask);
//
//        addTask(linearLiftTask);
//        goToJunction();
//
//        goliftMech();




    }
}