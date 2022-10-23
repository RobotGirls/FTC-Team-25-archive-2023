package opmodes;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import team25core.GamepadTask;
import team25core.RunToEncoderValueTask;
import team25core.MechanumGearedDrivetrain;
import team25core.OneWheelDirectDrivetrain;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import team25core.RobotEvent;
import team25core.StandardFourMotorRobot;
import team25core.TankMechanumControlSchemeFrenzy;
import team25core.TeleopDriveTask;
import opmodes.MecanumFieldCentricDriveScheme;

@TeleOp(name = "powerplayteleop")
//@Disabled
public class PowerPlayTeleop extends StandardFourMotorRobot {


    private TeleopDriveTask drivetask;

    private enum Direction {
        CLOCKWISE,
        COUNTERCLOCKWISE,
    }

    private BNO055IMU imu;

    //duck carousel
    private DcMotor carouselMech;

    //freight intake
    private DcMotor freightIntake;

    //mechanisms
    private DcMotor linearLift;
    private CRServo umbrella;

    private OneWheelDirectDrivetrain gravelLiftDrivetrain;

    private Telemetry.Item locationTlm;
    private Telemetry.Item buttonTlm;




    private boolean rotateDown = true;

    public static int DEGREES_DOWN = 575;
    public static int DEGREES_UP = 180;
    public static double GRAVELLIFT_POWER = -0.13;

    MecanumFieldCentricDriveScheme scheme;


    private MechanumGearedDrivetrain drivetrain;

    @Override
    public void handleEvent(RobotEvent e) {
    }




    @Override
    public void init() {

        super.init();
        initIMU();

        //mechs
        linearLift = hardwareMap.get(DcMotor.class, "linearLift");
//        umbrella = hardwareMap.crservo.get("umbrella");

        scheme = new MecanumFieldCentricDriveScheme(gamepad1,imu, this.telemetry);


        //code for forward mechanum drivetrain:
        drivetrain = new MechanumGearedDrivetrain(motorMap);
        drivetask = new TeleopDriveTask(this, scheme, frontLeft, frontRight, backLeft, backRight);
        drivetask.slowDown(true);

        locationTlm = telemetry.addData("location","init");
        buttonTlm = telemetry.addData("button", "n/a");


    }

    public void initIMU()
    {
        // Retrieve the IMU from the hardware map
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        // Technically this is the default, however specifying it is clearer
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        // Without this, data retrieving from the IMU throws an exception
        imu.initialize(parameters);

    }



    @Override
    public void start() {

        this.addTask(drivetask);
        locationTlm.setValue("in start");

        this.addTask(new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_1) {
            public void handleEvent(RobotEvent e) {
                GamepadEvent gamepadEvent = (GamepadEvent) e;
                locationTlm.setValue("in gamepad1 handler");
                switch (gamepadEvent.kind) {

                }
            }

        });

        //gamepad2 w /nowheels only mechs
        this.addTask(new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_2) {
            public void handleEvent(RobotEvent e) {
                GamepadEvent gamepadEvent = (GamepadEvent) e;
                locationTlm.setValue("in gamepad2 handler");
                switch (gamepadEvent.kind) {
                    case LEFT_BUMPER_DOWN:
                        linearLift.setPower(0.5);
                        break;
                    case LEFT_BUMPER_UP:
                        linearLift.setPower(0);
                        break;
                    case RIGHT_BUMPER_DOWN:
                        linearLift.setPower(-0.5);
                        break;
                    case RIGHT_BUMPER_UP:
                        linearLift.setPower(0);
                        break;
//                    case RIGHT_TRIGGER_DOWN:
//                        umbrella.setPower(0.5);
//                        break;
//                    case RIGHT_TRIGGER_UP:
//                        umbrella.setPower(0);
//                        break;
//                    case LEFT_TRIGGER_DOWN:
//                        umbrella.setPower(-0.5);
//                        break;
//                    case LEFT_TRIGGER_UP:
//                        umbrella.setPower(0);
//                        break;

                }
            }

        });

    }
}

