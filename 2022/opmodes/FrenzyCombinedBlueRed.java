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
import team25core.GamepadTask;
import team25core.Robot;
import team25core.RobotEvent;

@Autonomous(name = "FrenzyAutoMain")
//@Disabled

public class FrenzyCombinedBlueRed extends Robot {

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor rearLeft;
    private DcMotor rearRight;

    //private Servo teamElementServo;
    //private DcMotor carouselMech;

    private FourWheelDirectDrivetrain drivetrain;

    private GamepadTask gamepad;
    protected AllianceColor allianceColor;
    protected RobotPosition robotPosition;

    private enum AllianceColor {
        BLUE, // Button X
        RED, // Button B
        DEFAULT
    }

    private enum RobotPosition {
        STORAGE_UNIT, // Button Y
        WAREHOUSE, // Button A
        DEFAULT
    }

    //declaring telemetry item
    private Telemetry.Item allianceTlm;
    private Telemetry.Item positionTlm;


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

        if (e instanceof GamepadTask.GamepadEvent) {
            GamepadTask.GamepadEvent event = (GamepadTask.GamepadEvent) e;
            switch (event.kind) {
                case BUTTON_X_DOWN:
                    allianceColor = AllianceColor.BLUE;
                    allianceTlm.setValue("BLUE");
                    break;
                case BUTTON_B_DOWN:
                    allianceColor = AllianceColor.RED;
                    allianceTlm.setValue("RED");
                    redstorageunitpath();
                    break;
                case BUTTON_Y_DOWN:
                    robotPosition = RobotPosition.STORAGE_UNIT;
                    allianceTlm.setValue("STORAGE UNIT");
                    break;
                case BUTTON_A_DOWN:
                    robotPosition = RobotPosition.WAREHOUSE;
                    allianceTlm.setValue("WAREHOUSE");
                    break;
            }
        }
    }

    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        rearLeft = hardwareMap.get(DcMotor.class, "rearLeft");
        rearRight = hardwareMap.get(DcMotor.class, "rearRight");

        drivetrain = new FourWheelDirectDrivetrain(frontRight, rearRight, frontLeft, rearLeft);
    }

    @Override
    public void start() {

        if (allianceColor == AllianceColor.BLUE)
        {

            if (robotPosition == RobotPosition.STORAGE_UNIT) {
                bluestorageunitpath();

            } else if (robotPosition == RobotPosition.WAREHOUSE) {
                straightparkingwarehouse();

            }

        } else if (allianceColor == AllianceColor.RED) {

            if (robotPosition == RobotPosition.STORAGE_UNIT) {
                redstorageunitpath();

            } else if (robotPosition == RobotPosition.WAREHOUSE) {
                straightparkingwarehouse();

            }

        }
    }


    public void redstorageunitpath() {
        DeadReckonPath path = new DeadReckonPath();

        path.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 9, 1.0);

        path.addPause(5000);

        path.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 10, -1.0);

        path.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 31, 1.0);

        path.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 16, 1.0);

        this.addTask(new DeadReckonTask(this, path, drivetrain));
    }


    public void bluestorageunitpath() {
        DeadReckonPath path = new DeadReckonPath();

        path.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 9, 1.0);

        path.addPause(5000);

        path.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 10, -1.0);

        path.addSegment(DeadReckonPath.SegmentType.SIDEWAYS, 38, -1.0);

        path.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 10, 1.0);

        this.addTask(new DeadReckonTask(this, path, drivetrain));
    }

    public void straightparkingwarehouse() {
        DeadReckonPath path = new DeadReckonPath();

        path.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 20, 1.0);

        this.addTask(new DeadReckonTask(this, path, drivetrain));
    }

}





