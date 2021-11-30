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
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import team25core.DeadReckonPath;
import team25core.DeadReckonTask;
import team25core.FourWheelDirectDrivetrain;
import team25core.ObjectDetectionTask;
import team25core.ObjectImageInfo;
import team25core.OneWheelDirectDrivetrain;
import team25core.Robot;
import team25core.RobotEvent;

@Autonomous(name = "LM2AutoScore")
//@Disabled
//red side
public class LM2AutoScore extends Robot {

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor rearLeft;
    private DcMotor rearRight;

    //private Servo teamElementServo;
    private OneWheelDirectDrivetrain carouselDriveTrain;
    private DcMotor carouselMech;

    private OneWheelDirectDrivetrain flipOverDriveTrain;
    private DcMotor flipOver;

    private OneWheelDirectDrivetrain intakeMechDriveTrain;
    private DcMotor freightIntake;


    private DeadReckonPath goToShippingHubPath;

    private DeadReckonPath outTakePath;
    private DeadReckonPath goParkInWareHousePath;

    private DeadReckonPath leftBottomPath;
    private DeadReckonPath liftMechPathTop;
    private DeadReckonPath lowerMechPathTop;

    private DeadReckonPath middlePath;
    private DeadReckonPath liftMechPathMiddle;
    private DeadReckonPath lowerMechPathMiddle;

    private DeadReckonPath rightTopPath;
    private DeadReckonPath liftMechPathBottom;
    private DeadReckonPath lowerMechPathBottom;

    ObjectDetectionTask rdTask;
    ObjectImageInfo objectImageInfo;

    private Telemetry.Item currentLocationTlm;
    private Telemetry.Item pathTlm;
    private double objectConfidence;
    private String objectDetectionType;

    private int numTimesInHandleEvent;
    private String handleEventTlm;
    private Telemetry.Item objectSeenTlm;

    private FourWheelDirectDrivetrain drivetrain;


    /**
     * The default event handler for the robot.
     */
    @Override
    public void handleEvent(RobotEvent e)
    {
        /**
         * Every time we complete a segment drop a note in the robot log.
         */
        if (e instanceof DeadReckonTask.DeadReckonEvent) {
            RobotLog.i("Completed path segment %d", ((DeadReckonTask.DeadReckonEvent)e).segment_num);
        }
    }

    public void initPath()
    {
        // 1
        goToShippingHubPath = new DeadReckonPath();
        goToShippingHubPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 5, 1.0);

          // 6.35 - top  5- middle  3.5 - bottom

        outTakePath = new DeadReckonPath();
        outTakePath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 15, 1.0);


        goParkInWareHousePath = new DeadReckonPath();
        goParkInWareHousePath.addSegment(DeadReckonPath.SegmentType.TURN, 40, 1.0);
        goParkInWareHousePath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 40, -1.0);


        //top

        rightTopPath = new DeadReckonPath();
       // rightTopPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 40, 1.0);
        rightTopPath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 40, 0.5); //go right
        rightTopPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 40, 0.5); //go forward

        liftMechPathTop = new DeadReckonPath();
        liftMechPathTop.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 6.35, -1.0);

        lowerMechPathTop = new DeadReckonPath();
        lowerMechPathTop.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 6.35, 1.0);



        //middle

        middlePath = new DeadReckonPath();
        // middlePath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 40, 1.0);
        middlePath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 40, 0.5); //go right
        middlePath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 40, 0.5); //go forward


        liftMechPathMiddle = new DeadReckonPath();
        liftMechPathMiddle.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 5, -1.0);

        lowerMechPathMiddle = new DeadReckonPath();
        lowerMechPathMiddle.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 5, 1.0);

        // bottom

        leftBottomPath = new DeadReckonPath();
        //leftBottomPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 40, 1.0);
        leftBottomPath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 40, 0.5); //go right
        leftBottomPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 40, 0.5); //go forward

        liftMechPathBottom = new DeadReckonPath();
        liftMechPathBottom.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 3.5, -1.0);

        lowerMechPathBottom = new DeadReckonPath();
        lowerMechPathBottom.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 3.5, 1.0);








    }

    @Override
    public void init()
    {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        rearLeft = hardwareMap.get(DcMotor.class, "backLeft");
        rearRight = hardwareMap.get(DcMotor.class, "backRight");

        carouselMech = hardwareMap.get(DcMotor.class, "carouselMechR");

        carouselDriveTrain = new OneWheelDirectDrivetrain(carouselMech);
        carouselDriveTrain.resetEncoders();
        carouselDriveTrain.encodersOn();

        flipOver = hardwareMap.get(DcMotor.class, "flipOver");
        flipOverDriveTrain = new OneWheelDirectDrivetrain(flipOver);
        flipOverDriveTrain.resetEncoders();
        flipOverDriveTrain.encodersOn();

        //break for flipover
        flipOver.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        freightIntake = hardwareMap.get(DcMotor.class, "freightIntake");
        intakeMechDriveTrain = new OneWheelDirectDrivetrain(freightIntake);
        intakeMechDriveTrain.resetEncoders();
        intakeMechDriveTrain.encodersOn();

        // initializing paths
        initPath();

        drivetrain = new FourWheelDirectDrivetrain(frontRight, rearRight, frontLeft, rearLeft);
        drivetrain.resetEncoders();
        drivetrain.encodersOn();

        pathTlm = telemetry.addData("path status","unknown");

        objectImageInfo = new ObjectImageInfo();
        objectImageInfo.displayTelemetry(this.telemetry);


    }

    public void goToShippingHub()
    {
        this.addTask(new DeadReckonTask(this, goToShippingHubPath, drivetrain){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                   pathTlm.setValue("arrived at carousel");


                }
            }
        });

    }

    /////////////////////////////////////////////////// Top Methods /////////////////////////////////////////////////////////////////


    public void goMoveRightTop()
    {
        this.addTask(new DeadReckonTask(this, rightTopPath, drivetrain){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    pathTlm.setValue("arrived at carousel");
                    goliftMechTop();


                }
            }
        });

    }

    private void goliftMechTop()
    {
        this.addTask(new DeadReckonTask(this, liftMechPathTop, flipOverDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    pathTlm.setValue("done lifting");



                }
            }
        });
    }

    private void golowerMechTop()
    {
        this.addTask(new DeadReckonTask(this, lowerMechPathTop, intakeMechDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    pathTlm.setValue("done lowering");
                    goParkInWareHouse();



                }
            }
        });
    }

    /////////////////////////////////////////////////// Middle Methods /////////////////////////////////////////////////////////////////


    public void goMoveMiddle()
    {
        this.addTask(new DeadReckonTask(this, middlePath, drivetrain){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    pathTlm.setValue("arrived at carousel");
                    goliftMechMiddle();


                }
            }
        });

    }

    private void goliftMechMiddle()
    {
        this.addTask(new DeadReckonTask(this, liftMechPathMiddle, flipOverDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    pathTlm.setValue("done lifting");



                }
            }
        });
    }

    private void golowerMechMiddle()
    {
        this.addTask(new DeadReckonTask(this, lowerMechPathMiddle, intakeMechDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    pathTlm.setValue("done lowering");




                }
            }
        });
    }

    /////////////////////////////////////////////////// Bottom Methods /////////////////////////////////////////////////////////////////


    public void goMoveLeftBottom()
    {
        this.addTask(new DeadReckonTask(this, leftBottomPath, drivetrain){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    pathTlm.setValue("arrived at carousel");
                    goliftMechBottom();


                }
            }
        });



    }

    private void goliftMechBottom()
    {
        this.addTask(new DeadReckonTask(this, liftMechPathBottom, flipOverDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    pathTlm.setValue("done lifting");



                }
            }
        });
    }

    private void golowerMechBottom()
    {
        this.addTask(new DeadReckonTask(this, lowerMechPathBottom, intakeMechDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    pathTlm.setValue("done lowering");
                    goParkInWareHouse();



                }
            }
        });
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void goOuttakePreloaded()
    {
        this.addTask(new DeadReckonTask(this, outTakePath, intakeMechDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    pathTlm.setValue("done lifting");



                }
            }
        });
    }



    public void goParkInWareHouse()
    {
        this.addTask(new DeadReckonTask(this, goParkInWareHousePath, drivetrain){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    pathTlm.setValue("parked in Warehouse");


                }
            }
        });

    }

    public void setObjectDetection()
    {
        rdTask = new ObjectDetectionTask(this,"Webcam1")
        {
            @Override
            public void handleEvent(RobotEvent e)
            {
                ObjectDetectionEvent event = (ObjectDetectionEvent) e;
                objectImageInfo.getImageInfo(event);
                objectConfidence = objectImageInfo.getConfidence();
                objectDetectionType = objectImageInfo.getObjDetectionType();
                currentLocationTlm.setValue("in ObjectDetectionTask handleEvent");


                if(event.kind == EventKind.OBJECTS_DETECTED)
                {
                    objectSeenTlm.setValue(objectDetectionType);

                    if (objectDetectionType.equals("topCap") )
                    {
                        objectSeenTlm.setValue("topCap");
                        goMoveRightTop();

                    }
                    else if (objectDetectionType.equals("middleCap") )
                    {
                        objectSeenTlm.setValue("middleCap");
                        goMoveMiddle();

                    }
                    else if (objectDetectionType.equals("bottomCap") )
                    {
                        objectSeenTlm.setValue("bottomCap");
                        goMoveLeftBottom();

                    }
                    else
                        objectSeenTlm.setValue("no objects Seen");
                }

            }
        };

    }



    @Override
    public void start()
    {
        DeadReckonPath path = new DeadReckonPath();
        goToShippingHub();

    }



}
