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
import team25core.OneWheelDirectDrivetrain;
import team25core.Robot;
import team25core.RobotEvent;

@Autonomous(name = "LM2WareHouseAuto")
//@Disabled
public class LM2WareHouseAuto extends Robot {

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
    private DeadReckonPath liftMechPath;
    private DeadReckonPath outTakePath;
    private DeadReckonPath lowerMechPath;
    private DeadReckonPath goParkInWareHousePath;


    private Telemetry.Item pathTlm;

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

        liftMechPath = new DeadReckonPath();
        liftMechPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 3.5, -1.0);  // 6.35 - top  5- middle  3.5 - bottom

        outTakePath = new DeadReckonPath();
        outTakePath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 15, 1.0);

        lowerMechPath = new DeadReckonPath();
        lowerMechPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 3.5, 1.0);

        goParkInWareHousePath = new DeadReckonPath();
        goParkInWareHousePath.addSegment(DeadReckonPath.SegmentType.TURN, 40, 1.0);
        goParkInWareHousePath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 40, -1.0);




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
                    goliftMech();

                }
            }
        });

    }


    private void goliftMech()
    {
        this.addTask(new DeadReckonTask(this, liftMechPath, flipOverDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    pathTlm.setValue("done lifting");
                    goOuttakePreloaded();


                }
            }
        });
    }



    private void goOuttakePreloaded()
    {
        this.addTask(new DeadReckonTask(this, outTakePath, intakeMechDriveTrain) {
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE) {
                    pathTlm.setValue("done lifting");
                    golowerMech();


                }
            }
        });
    }

    private void golowerMech()
    {
        this.addTask(new DeadReckonTask(this, lowerMechPath, intakeMechDriveTrain) {
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



    @Override
    public void start()
    {
        DeadReckonPath path = new DeadReckonPath();
        goToShippingHub();

    }



}