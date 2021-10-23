package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import team25core.DeadmanMotorTask;
import team25core.GamepadTask;
import team25core.Robot;
import team25core.RobotEvent;
import team25core.SingleGamepadControlScheme;
import team25core.SingleShotTimerTask;
import team25core.StandardFourMotorRobot;
import team25core.TeleopDriveTask;

@TeleOp(name = "FreightFrenzyTeleop")
//@Disabled
public class FrenzyTeleop extends StandardFourMotorRobot {


    private TeleopDriveTask drivetask;

//    private DcMotor frontLeft;
//    private DcMotor frontRight;
//    private DcMotor backLeft;
//    private DcMotor backRight;


    //duck carousel
    private DcMotor carouselMech;

    //freight intake
    //private DcMotor freightIntake;
    private DcMotor flipOver;
    private Servo intakeDrop;
    private boolean intakeDropOpen = false;

    //    private final double OPEN_DROP_SERVO = (float) ;
//    private final double CLOSE_DROP_SERVO = (float) ;
//    private final double INTAKE_OUT = 1;
//    private final double INTAKE_IN = -1;
//    private final double INTAKE_STOP = 0;
    private final double FLIP_FORWARD_DIRECTION = 0.1;
    private final double FLIP_BACKWARD_DRECTION = -0.1;

    //changing direction for flip mechanism
    DeadmanMotorTask flipForward;
    DeadmanMotorTask flipBackward;

    //private FourWheelDirectDrivetrain drivetrain;
    //private MechanumGearedDrivetrain drivetrain;

    @Override
    public void handleEvent(RobotEvent e) {
    }

    @Override
    public void init() {

        super.init();

        //mapping the wheels
//        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
//        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
//        backLeft = hardwareMap.get(DcMotorEx.class, "rearLeft");
//        backRight = hardwareMap.get(DcMotorEx.class, "rearRight");

        //mapping carousel mech
        carouselMech = hardwareMap.get(DcMotor.class, "carouselMech");

        //mapping freight intake mech
        //freightIntake = hardwareMap.get(DcMotor.class, "freightIntake");
        flipOver = hardwareMap.get(DcMotor.class, "flipOver");
//        intakeDrop = hardwareMap.servo.get("intakeDrop");


        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        carouselMech.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //freightIntake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flipOver.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


       /* launch = new OneWheelDirectDrivetrain(launchMech);
        launch.resetEncoders();
        launch.encodersOn();

        intake = new OneWheelDirectDrivetrain(intakeMech);
        intake.resetEncoders();
        intake.encodersOn();

        */

        //code for forward mechanum drivetrain:
        //drivetrain = new MechanumGearedDrivetrain(360, frontRight, rearRight, frontLeft, rearLeft);
    }

    @Override
    public void start() {

        SingleGamepadControlScheme scheme = new SingleGamepadControlScheme(gamepad1);

        drivetask = new TeleopDriveTask(this, scheme, frontLeft, frontRight, backLeft, backRight);

        this.addTask(drivetask);

        this.addTask(new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_1) {
            //@Override
            public void handleEvent(RobotEvent e) {
                GamepadEvent gamepadEvent = (GamepadEvent) e;

                switch (gamepadEvent.kind) {
                    //launching system
                    case RIGHT_TRIGGER_DOWN:
                        //moving carousel
                        carouselMech.setPower(1);
                        break;
                    case RIGHT_TRIGGER_UP:
                        //STOPPING CAROUSEL
                        carouselMech.setPower(0);
                        break;
                    case LEFT_TRIGGER_DOWN:
                        //moving carousel
                        carouselMech.setPower(-1);
                        break;
                    case LEFT_TRIGGER_UP:
                        //STOPPING CAROUSEL
                        carouselMech.setPower(0);
                        break;
//                    case RIGHT_BUMPER_DOWN:
//                        //moving flaps forward
//                        freightIntake.setPower(INTAKE_IN);
//                        break;
//                    case RIGHT_BUMPER_UP:
//                        freightIntake.setPower(0);
//                        break;
//                    case LEFT_BUMPER_DOWN:
//                        //moving flaps backward
//                        freightIntake.setPower(INTAKE_OUT);
//                        break;
//                    case LEFT_BUMPER_UP:
//                        freightIntake.setPower(0);
//                        break;
                    case BUTTON_Y_DOWN:
                        flipOver.setPower(FLIP_FORWARD_DIRECTION);
                        break;
                    case BUTTON_Y_UP:
                        flipOver.setPower(FLIP_BACKWARD_DRECTION);
                        break;
//                    case BUTTON_A_DOWN:
//                        //servo holding the freight inside intake
//                        if (intakeDropOpen) {
//                            intakeDrop.setPosition(CLOSE_DROP_SERVO);
//                            intakeDropOpen = false;
//                        } else {
//                            intakeDrop.setPosition(OPEN_DROP_SERVO);
//                            intakeDropOpen = true;
//                        }
//                        break;
                }
            }

        });
    }
}
