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
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import team25core.DeadReckonPath;
import team25core.FourWheelDirectDrivetrain;
import team25core.ObjectDetectionTask;
import team25core.ObjectImageInfo;
import team25core.OneWheelDirectDrivetrain;
import team25core.Robot;
import team25core.RobotEvent;
import team25core.SingleShotTimerTask;

@Autonomous(name = "QT1AutoCR1")
//@Disabled
//red side
public class QT1AutoCR extends Robot {

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor rearLeft;
    private DcMotor rearRight;
    private Servo intakeDrop;


    // for timer
    private static double INTAKEDROP_OPEN = 180 / 256.0;
    private static double INTAKEDROP_OUT = 1 / 256.0;
    private final static int PAUSE_TIMER = 1000;
    SingleShotTimerTask rtTask;
    private String whichPause = "unknown";

    //private Servo teamElementServo;
    private OneWheelDirectDrivetrain carouselDriveTrain;
    private DcMotor carouselMech;

    private OneWheelDirectDrivetrain gravelLiftDriveTrain;
    private DcMotor gravelLift;

    private OneWheelDirectDrivetrain intakeMechDriveTrain;
    private DcMotor freightIntake;

    private DeadReckonPath outTakePath;
    private DeadReckonPath goToCarouselPath;

    private DeadReckonPath moveToShippingHub;
    private DeadReckonPath turningCarouselPath;
    private DeadReckonPath goStrafeLeftPath;
    private DeadReckonPath goParkPath;

    private DeadReckonPath goMoveForwardTopPath;
    private DeadReckonPath liftMechPathTop;
    private DeadReckonPath lowerMechPathTop;
    private DeadReckonPath goParkTopPath;

    private DeadReckonPath liftMechPathMiddle;
    private DeadReckonPath lowerMechPathMiddle;


    private DeadReckonPath liftMechPathBottom;
    private DeadReckonPath lowerMechPathBottom;
    private DeadReckonPath goMoveForwardBottomPath;

    private DeadReckonPath goliftMechInitalPath;
    private DeadReckonPath goliftDownMechInitalPath;




    //detection
    private double capPositionLeft;
    private double capMidpoint;
    private double capImageWidth;

    private String capStonePos;

    private double capLocation;

    ObjectDetectionTask rdTask;
    ObjectImageInfo objectImageInfo;

    private Telemetry.Item currentLocationTlm;
    private Telemetry.Item pathTlm;
    private Telemetry.Item positionTlm;
    private Telemetry.Item objectDetectedTlm;

    private FourWheelDirectDrivetrain drivetrain;


    /**
     * The default event handler for the robot.
     */
    @Override
    public void handleEvent(RobotEvent e) {
        /**
         * Every time we complete a segment drop a note in the robot log.
         */
        if (e instanceof DeadReckonTask.DeadReckonEvent) {
            RobotLog.i("Completed path segment %d", ((DeadReckonTask.DeadReckonEvent) e).segment_num);
        }
    }

    public void initPath() {
        // 1
        goToCarouselPath = new DeadReckonPath();
        //goToCarouselPath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 6, -1.0);
        goToCarouselPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 4.5, -0.35);
        goToCarouselPath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 16, 0.5);
        goToCarouselPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 4, 0.15);



        // 2
        turningCarouselPath = new DeadReckonPath();
        turningCarouselPath.stop();
        turningCarouselPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 60, 1.0);

        //3
//        goBackOriginalLocationPath = new DeadReckonPath();
//        goBackOriginalLocationPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 7.5, 1.0);

        goStrafeLeftPath = new DeadReckonPath();
        //goStrafeLeftPath.addSegment(DeadReckonPath.SegmentType.TURN, 5, -0.5);
        goStrafeLeftPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 15, -0.25);
        goStrafeLeftPath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 4.3, -0.25);
        goStrafeLeftPath.addSegment(DeadReckonPath.SegmentType.TURN, 24, -0.25);
        goStrafeLeftPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 10, 0.3);
        goStrafeLeftPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 16, -0.25);

        goMoveForwardTopPath = new DeadReckonPath();
        goMoveForwardTopPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 15, -0.25);
        goMoveForwardTopPath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 15, -0.5);

        goParkTopPath = new DeadReckonPath();
        goParkTopPath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 8 , 0.5);
        goParkTopPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 4, -1);


        goParkPath = new DeadReckonPath();
        goParkPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 8, 1);
        goParkPath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 4 , -0.5);

        //outtaking object
        outTakePath = new DeadReckonPath();
        outTakePath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 15, 1.0);


        // move sideways to shipping hub after detection
        moveToShippingHub = new DeadReckonPath();
        moveToShippingHub.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 7, -1.0);


        // 6.35 - top  5- middle  3.5 - bottom

        goliftMechInitalPath = new DeadReckonPath();
        goliftMechInitalPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 3, -0.25);


        //top

        liftMechPathTop = new DeadReckonPath();
        liftMechPathTop.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 7.5, 0.07);

        lowerMechPathTop = new DeadReckonPath();
        lowerMechPathTop.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 7.5, -0.07);

        //middle

        liftMechPathMiddle = new DeadReckonPath();
        liftMechPathMiddle.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 3.5, 0.07);

        lowerMechPathMiddle = new DeadReckonPath();
        lowerMechPathMiddle.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 3.5, -0.07);
        // bottom

        goMoveForwardBottomPath = new DeadReckonPath();
        goMoveForwardBottomPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 1.5, -0.25);

        liftMechPathBottom = new DeadReckonPath();
        liftMechPathBottom.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 1.7, 0.07);

        lowerMechPathBottom = new DeadReckonPath();
        lowerMechPathBottom.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 1.7, -0.07);


        goliftDownMechInitalPath = new DeadReckonPath();
        goliftDownMechInitalPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 4.5, 0.25);



    }

    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        rearLeft = hardwareMap.get(DcMotor.class, "backLeft");
        rearRight = hardwareMap.get(DcMotor.class, "backRight");

        drivetrain = new FourWheelDirectDrivetrain(frontRight, rearRight, frontLeft, rearLeft);
        drivetrain.resetEncoders();
        drivetrain.encodersOn();

        carouselMech = hardwareMap.get(DcMotor.class, "carouselMech");
        carouselDriveTrain = new OneWheelDirectDrivetrain(carouselMech);
        carouselDriveTrain.resetEncoders();
        carouselDriveTrain.encodersOn();

        gravelLift = hardwareMap.get(DcMotor.class, "gravelLift");
        gravelLiftDriveTrain = new OneWheelDirectDrivetrain(gravelLift);
        gravelLiftDriveTrain.resetEncoders();
        gravelLiftDriveTrain.encodersOn();

        intakeDrop = hardwareMap.servo.get("intakeDrop");
        intakeDrop.setPosition(INTAKEDROP_OPEN);

        //break for flipover
        gravelLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



        freightIntake = hardwareMap.get(DcMotor.class, "freightIntake");
        intakeMechDriveTrain = new OneWheelDirectDrivetrain(freightIntake);
        intakeMechDriveTrain.resetEncoders();
        intakeMechDriveTrain.encodersOn();

        // initializing paths


        pathTlm = telemetry.addData("path status", "unknown");

        objectImageInfo = new ObjectImageInfo();
        objectImageInfo.displayTelemetry(this.telemetry);

        currentLocationTlm = telemetry.addData("current location", "init");
        objectDetectedTlm = telemetry.addData("Object detected", "unknown");

        positionTlm = telemetry.addData("Position:", "unknown");


        initPath();
        setObjectDetection();
        addTask(rdTask);
        //rdTask.start();


    }

    public void startPauseTimer() {
        pathTlm.setValue("in starting timer");
        rtTask = new SingleShotTimerTask(this, PAUSE_TIMER)
        {
            @Override
            public void handleEvent(RobotEvent e)
            {
                SingleShotTimerEvent event = (SingleShotTimerEvent) e;

                if(event.kind == EventKind.EXPIRED)
                {

                    pathTlm.setValue("timer expired");
                    intakeDrop.setPosition(INTAKEDROP_OPEN);
                    if ( capStonePos == "bottom")
                    {

                        golowerMechBottom();
                    }
                    else if ( capStonePos == "middle")
                    {


                        golowerMechMiddle();

                    }


                    //no need for servo timer for top pos


                }
            }
        };

        addTask(rtTask);
    }

    public void setObjectDetection() {
        rdTask = new ObjectDetectionTask(this, "Webcam1") {
            @Override
            public void handleEvent(RobotEvent e) {

                ObjectDetectionEvent event = (ObjectDetectionEvent) e;
                capPositionLeft = event.objects.get(0).getLeft();
                capMidpoint = (event.objects.get(0).getWidth() / 2.0) + capPositionLeft;
                capImageWidth = event.objects.get(0).getImageWidth();
                if (event.kind == EventKind.OBJECTS_DETECTED) {
                    objectDetectedTlm.setValue(event.objects.get(0).getLabel());
                    currentLocationTlm.setValue(capMidpoint);
                    capLocation = capMidpoint;
                }
            }
        };
        rdTask.init(telemetry, hardwareMap);
        rdTask.setDetectionKind(ObjectDetectionTask.DetectionKind.EVERYTHING);

    }




    /////////////////////////////////////////////////// Top Methods /////////////////////////////////////////////////////////////////


    public void goMovetoHubTop()
    {
        this.addTask(new DeadReckonTask(this, goMoveForwardTopPath, drivetrain){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    pathTlm.setValue("arrived at hub");
                    goliftMechTop();

                }
            }
        });

    }
    private void goliftMechTop() {
        this.addTask(new DeadReckonTask(this, liftMechPathTop, gravelLiftDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    pathTlm.setValue("done lifting");
                    golowerMechTop();

                }
            }
        });
    }


    private void golowerMechTop() {
        this.addTask(new DeadReckonTask(this, lowerMechPathTop, gravelLiftDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    pathTlm.setValue("done lowering");
                    goPark();


                }
            }
        });
    }

    /////////////////////////////////////////////////// Middle Methods /////////////////////////////////////////////////////////////////


    private void goliftMechMiddle() {
        this.addTask(new DeadReckonTask(this, liftMechPathMiddle, gravelLiftDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    pathTlm.setValue("done lifting");
                    intakeDrop.setPosition(INTAKEDROP_OUT);
                    startPauseTimer();




                }
            }
        });
    }

    private void golowerMechMiddle() {
        this.addTask(new DeadReckonTask(this, lowerMechPathMiddle, gravelLiftDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    pathTlm.setValue("done lowering");
                    goPark();



                }
            }
        });
    }

    /////////////////////////////////////////////////// Bottom Methods /////////////////////////////////////////////////////////////////

//    public void goMoveForwardBottom() {
//        this.addTask(new DeadReckonTask(this, goMoveForwardBottomPath, drivetrain) {
//            @Override
//            public void handleEvent(RobotEvent e) {
//                DeadReckonEvent path = (DeadReckonEvent) e;
//                if (path.kind == EventKind.PATH_DONE) {
//                    pathTlm.setValue("arrived at carousel");
//                    goliftMechBottom();
//
//
//                }
//            }
//        });
//
//    }

    private void goliftMechBottom() {
        this.addTask(new DeadReckonTask(this, liftMechPathBottom, gravelLiftDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    pathTlm.setValue("done lifting");
                    intakeDrop.setPosition(INTAKEDROP_OUT);
                    startPauseTimer();


                }
            }
        });
    }

    private void golowerMechBottom() {
        this.addTask(new DeadReckonTask(this, lowerMechPathBottom, gravelLiftDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    pathTlm.setValue("done lowering");
                    goParkTop();


                }
            }
        });
    }

    public void goParkTop()
    {
        this.addTask(new DeadReckonTask(this, goParkTopPath, drivetrain){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    pathTlm.setValue("strafe left done");



                }
            }
        });

    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////







    public void goToCarousel()
    {
        this.addTask(new DeadReckonTask(this, goToCarouselPath, drivetrain){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    pathTlm.setValue("arrived at carousel");
                    spinCarousel(capStonePos);

                }
            }
        });

    }

    private void spinCarousel(String capPosition)
    {
        this.addTask(new DeadReckonTask(this, turningCarouselPath, carouselDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    pathTlm.setValue("done spinning carousel");
                    if ( capPosition == "bottom")
                    {
                        goStrafeLeft(capStonePos);
                    }
                    else if ( capPosition == "middle")
                    {
                        goStrafeLeft(capStonePos);
                    }
                    else if ( capPosition == "top")
                    {
                        goMovetoHubTop();
                    }

                }
            }
        });
    }


    public void goStrafeLeft(String capPosition)
    {
        this.addTask(new DeadReckonTask(this, goStrafeLeftPath, drivetrain){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    pathTlm.setValue("strafe left done");
                    if (path.kind == EventKind.PATH_DONE) {
                        pathTlm.setValue("arrived at carousel");

                        if ( capPosition == "bottom")
                        {
                            goliftMechBottom();
                        }
                        else if ( capPosition == "middle")
                        {
                            goliftMechMiddle();
                        }


                    }

                }
            }
        });

    }

    public void goPark()
    {
        this.addTask(new DeadReckonTask(this, goParkPath, drivetrain){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    pathTlm.setValue("strafe left done");



                }
            }
        });

    }

    @Override
    public void start() {

        if (capLocation < 340) {
            positionTlm.setValue("Bottom Position");
            capStonePos = "bottom";


        } else if (capLocation < 580) {
            positionTlm.setValue("Middle Position");
            capStonePos = "middle";

        } else {
            positionTlm.setValue("Top Position");
            capStonePos = "top";
        }

        goToCarousel();




    }



}