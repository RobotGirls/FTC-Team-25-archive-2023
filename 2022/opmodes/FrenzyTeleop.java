package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.lang.invoke.MethodHandle;

import team25core.DeadmanMotorTask;
import team25core.GamepadTask;
import team25core.MechanumGearedDrivetrain;
import team25core.OneWheelDirectDrivetrain;
import team25core.Robot;
import team25core.RobotEvent;
import team25core.RunToEncoderValueTask;
import team25core.SingleGamepadControlScheme;
import team25core.SingleShotTimerTask;
import team25core.StandardFourMotorRobot;
import team25core.TankMechanumControlScheme;
//import team25core.TankMechanumControlSchemeBackwards;
//import team25core.TankMechanumControlSchemeReverse;
import team25core.TeleopDriveTask;

@TeleOp(name = "FreightFrenzyTeleop")
//@Disabled
public class FrenzyTeleop extends StandardFourMotorRobot {


    private TeleopDriveTask drivetask;

    private enum Direction {
        CLOCKWISE,
        COUNTERCLOCKWISE,
    }


    //duck carousel
    private DcMotor carouselMechOne;
    private DcMotor carouselMechTwo;

    //freight intake
    private DcMotor freightIntake;
    private Servo intakeDrop;
    private boolean intakeDropOpen = false;



    //changing direction for flip mechanism
    private DcMotor flipOver;
    private OneWheelDirectDrivetrain flipOverDrivetrain;
    public static int DEGREES_DOWN = 1600;
    public static int DEGREES_UP = 180;
    public static double FLIPOVER_POWER = 0.3;
    private boolean rotateDown = true;
    TankMechanumControlScheme scheme;


    private MechanumGearedDrivetrain drivetrain;

    @Override
    public void handleEvent(RobotEvent e) {
    }

    //flipover positions for bottom and top positions for intake and placing on hubs
    private void rotateFlipOver(Direction direction) {
        if (direction == FrenzyTeleop.Direction.CLOCKWISE) {
            flipOver.setDirection(DcMotorSimple.Direction.REVERSE);
        } else {
            flipOver.setDirection(DcMotorSimple.Direction.FORWARD);
        }
        this.addTask(new RunToEncoderValueTask(this, flipOver, DEGREES_DOWN, FLIPOVER_POWER));
    }

    //alternates between down and up positions.
    private void alternateRotate() {
        if (rotateDown) {       // happens first
            rotateFlipOver(FrenzyTeleop.Direction.CLOCKWISE);
            rotateDown = false;

        } else {
            rotateFlipOver(FrenzyTeleop.Direction.COUNTERCLOCKWISE);
            rotateDown = true;
        }
    }


    @Override
    public void init() {

        super.init();

        //mapping carousel mech
        carouselMechOne = hardwareMap.get(DcMotor.class, "carouselMechR");
        carouselMechTwo = hardwareMap.get(DcMotor.class, "carouselMechL");

        //mapping freight intake mech
        freightIntake = hardwareMap.get(DcMotor.class, "freightIntake");
        flipOver = hardwareMap.get(DcMotor.class, "flipOver");
//      intakeDrop = hardwareMap.servo.get("intakeDrop");

        // reset encoders
        carouselMechOne.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        carouselMechTwo.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        freightIntake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flipOver.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flipOver.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //allows for flipOver moter to hold psoition when no button is being pressed
        flipOver.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        flipOverDrivetrain = new OneWheelDirectDrivetrain(flipOver);

        scheme = new TankMechanumControlScheme(gamepad1);


        //code for forward mechanum drivetrain:
        drivetrain = new MechanumGearedDrivetrain(motorMap);
        drivetask = new TeleopDriveTask(this, scheme, frontLeft, frontRight, backLeft, backRight);

    }

    @Override
    public void start() {

        this.addTask(drivetask);

        //gamepad1 w /wheels
        this.addTask(new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_1) {
            //@Override
            public void handleEvent(RobotEvent e) {
                GamepadEvent gamepadEvent = (GamepadEvent) e;

                switch (gamepadEvent.kind) {
                    //launching system
                    case RIGHT_TRIGGER_DOWN:
                        //moving carousel
                        carouselMechOne.setPower(-1);
                        carouselMechTwo.setPower(1);
                        break;
                    case RIGHT_TRIGGER_UP:
                        //STOPPING CAROUSEL
                        carouselMechOne.setPower(0);
                        carouselMechTwo.setPower(0);
                        break;
                    case LEFT_TRIGGER_DOWN:
                        //moving carousel
                        carouselMechOne.setPower(1);
                        carouselMechTwo.setPower(-1);
                        break;
                    case LEFT_TRIGGER_UP:
                        //STOPPING CAROUSEL
                        carouselMechTwo.setPower(0);
                        carouselMechOne.setPower(0);
                        break;
                    case RIGHT_BUMPER_DOWN:
                        //moving flaps forward
                        freightIntake.setPower(1);
                        break;
                    case RIGHT_BUMPER_UP:
                        freightIntake.setPower(0);
                        break;
                    case LEFT_BUMPER_DOWN:
                        //moving flaps backward
                        freightIntake.setPower(-1);
                        break;
                    case LEFT_BUMPER_UP:
                        freightIntake.setPower(0);
                        break;
//                    case BUTTON_Y_DOWN:
//                        alternateRotate();
//                        break;
                    case BUTTON_B_DOWN:
                        flipOver.setPower(0.4);
                        break;
                    case BUTTON_B_UP:
                        flipOver.setPower(0);
                        break;
                    case BUTTON_X_DOWN:
                        flipOver.setPower(-0.4);
                        break;
                    case BUTTON_X_UP:
                        flipOver.setPower(0);
                        break;
                }
            }

        });

        //gamepad2 w /nowheels only mechs
        this.addTask(new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_2) {
            public void handleEvent(RobotEvent e) {
                GamepadEvent gamepadEvent = (GamepadEvent) e;

                switch (gamepadEvent.kind) {
                    //launching system
                    case RIGHT_TRIGGER_DOWN:
                        //moving carousel
                        carouselMechOne.setPower(-1);
                        carouselMechTwo.setPower(1);
                        break;
                    case RIGHT_TRIGGER_UP:
                        //STOPPING CAROUSEL
                        carouselMechOne.setPower(0);
                        carouselMechTwo.setPower(0);
                        break;
                    case LEFT_TRIGGER_DOWN:
                        //moving carousel
                        carouselMechOne.setPower(1);
                        carouselMechTwo.setPower(-1);
                        break;
                    case LEFT_TRIGGER_UP:
                        //STOPPING CAROUSEL
                        carouselMechTwo.setPower(0);
                        carouselMechOne.setPower(0);
                        break;
                    case RIGHT_BUMPER_DOWN:
                        //moving flaps forward
                        freightIntake.setPower(1);
                        break;
                    case RIGHT_BUMPER_UP:
                        freightIntake.setPower(0);
                        break;
                    case LEFT_BUMPER_DOWN:
                        //moving flaps backward
                        freightIntake.setPower(-1);
                        break;
                    case LEFT_BUMPER_UP:
                        freightIntake.setPower(0);
                        break;
//                    case BUTTON_Y_DOWN:
//                        alternateRotate();
//                        break;
                    case BUTTON_B_DOWN:
                        flipOver.setPower(0.4);
                        break;
                    case BUTTON_B_UP:
                        flipOver.setPower(0);
                        break;
                    case BUTTON_X_DOWN:
                        flipOver.setPower(-0.4);
                        break;
                    case BUTTON_X_UP:
                        flipOver.setPower(0);
                        break;


                }
            }

        });

    }
}
