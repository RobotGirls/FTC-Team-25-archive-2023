package TestRig;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Arrays;
import java.util.List;

import team25core.GamepadTask;
import team25core.Robot;
import team25core.RunToEncoderValueTask;

public class IndepMotorMode {
    public enum MotorDirection {
        STOP,
        CLOCKWISE,
        COUNTER_CLOCKWISE;

        public IndepMotorMode.MotorDirection nextDirection() {
            IndepMotorMode.MotorDirection currentDirection = values()[ordinal()];

            if(currentDirection != COUNTER_CLOCKWISE) {
                return values()[ordinal() +1];
            } else {
                return values()[0];
            }
        }
    }

    //Temporary motor limit need to look up or make configurable
    static final double MAX_MOTOR_LIMIT = 100;
    static final double MIN_MOTOR_LIMIT = 0;
    static final int MOTOR_ABS_POS_INIT_POSITION = 0;
    static final int MOTOR_ABS_POS_NOT_SET = -1;


    Robot myRobot;
    private DcMotor motor1;
    private DcMotor motor2;
    private DcMotor motor3;
    private DcMotor motor4;

    private Telemetry.Item motor1DirectionTlm;
    private Telemetry.Item motor2DirectionTlm;
    private Telemetry.Item motor3DirectionTlm;
    private Telemetry.Item motor4DirectionTlm;

    private MotorDirection motor1Direction;
    private MotorDirection motor2Direction;
    private MotorDirection motor3Direction;
    private MotorDirection motor4Direction;

    private int motorPosDelta;
    private double motorPower;
    private int motorPosAbsolute = MOTOR_ABS_POS_NOT_SET;


    //constructor to instantiate independent motor object
    public IndepMotorMode (Robot robot) {
        myRobot = robot;
        motorPosDelta = 5;

        motor1 = myRobot.hardwareMap.get(DcMotor.class, "motor1");
        motor2 = myRobot.hardwareMap.get(DcMotor.class, "motor2");
        motor3 = myRobot.hardwareMap.get(DcMotor.class, "motor3");
        motor4 = myRobot.hardwareMap.get(DcMotor.class, "motor4");

        motor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor4.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motor1Direction = MotorDirection.STOP;
        motor2Direction = MotorDirection.STOP;
        motor3Direction = MotorDirection.STOP;
        motor4Direction = MotorDirection.STOP;

        motor1DirectionTlm = myRobot.telemetry.addData("motor1 Direction", motor1Direction);
        motor2DirectionTlm = myRobot.telemetry.addData("motor2 Direction", motor2Direction);
        motor3DirectionTlm = myRobot.telemetry.addData("motor3 Direction", motor3Direction);
        motor4DirectionTlm = myRobot.telemetry.addData("motor4 Direction", motor4Direction);

    }

    public void setDirection(MotorDirection direction, DcMotor motor, Telemetry.Item motorDirectionTlm) {
        if(direction == MotorDirection.CLOCKWISE) {
            motor.setDirection(DcMotor.Direction.FORWARD);
        } else if (direction == MotorDirection.COUNTER_CLOCKWISE) {
            motor.setDirection(DcMotor.Direction.REVERSE);
        }
        motorDirectionTlm.setValue(direction);
        return;
    }

    private void setNewMotorPosition(DcMotor motor, Telemetry.Item motorPosTlm) {
        double currentMotorPosition;
        double newMotorPosition;
        DcMotor.Direction motorDirection;

        currentMotorPosition = motor.getCurrentPosition();
        motorDirection = motor.getDirection();

        newMotorPosition = currentMotorPosition;

        if(motorDirection == DcMotor.Direction.FORWARD) {
            newMotorPosition = currentMotorPosition + motorPosDelta;
            if (newMotorPosition > MAX_MOTOR_LIMIT) {
                newMotorPosition = MAX_MOTOR_LIMIT;
            }
        } else if (motorDirection == DcMotor.Direction.REVERSE) {
            newMotorPosition = currentMotorPosition - motorPosDelta;
            if (newMotorPosition < MIN_MOTOR_LIMIT) {
                newMotorPosition = MIN_MOTOR_LIMIT;
            }
        }

        //command motor to new position and print telemetry to phone
        motor.setTargetPosition((int) newMotorPosition);
        motorPosTlm.setValue(newMotorPosition);
        return;

    }

    private void driveMotors(DcMotor motor1, DcMotor motor2, DcMotor motor3, DcMotor motor4 ) {
        int newMotorPosition;
        int currMotorPosition;
        DcMotor.Direction motorDirection;

        //create list of motors
        List<DcMotor> motorList = Arrays.asList(motor1, motor2, motor3, motor4);
        //loop through all motors and set new motor position

        for(DcMotor motor : motorList) {
            currMotorPosition = motor.getCurrentPosition();
            motorDirection = motor.getDirection();
            newMotorPosition = currMotorPosition;
            if(motorDirection == DcMotor.Direction.FORWARD) {
                newMotorPosition = currMotorPosition + motorPosDelta;
            } else if(motorDirection == DcMotorSimple.Direction.REVERSE) {
                newMotorPosition = currMotorPosition - motorPosDelta;
            }

            myRobot.addTask(new RunToEncoderValueTask(myRobot, motor, newMotorPosition, motorPower));
        }
    }

    protected void setUpGamepadForMotorControl(GamepadTask .GamepadEvent event) {

        // casting instance of myRobot(generic robot type) into TestRigDemo type
        TestRigDemo demoInstance = (TestRigDemo) myRobot;

        switch (event.kind) {
            case BUTTON_Y_DOWN:
                //select motor1
                motor1Direction = motor1Direction.nextDirection();
                motor1DirectionTlm.setValue(motor1Direction);
                setDirection(motor1Direction, motor1, motor1DirectionTlm);
                break;
            case BUTTON_A_DOWN:
                //select motor2
                motor2Direction = motor2Direction.nextDirection();
                motor2DirectionTlm.setValue(motor2Direction);
                setDirection(motor2Direction, motor2, motor2DirectionTlm);
                break;
            case BUTTON_B_DOWN:
                //select motor3
                motor3Direction = motor3Direction.nextDirection();
                motor3DirectionTlm.setValue(motor3Direction);
                setDirection(motor3Direction, motor3, motor3DirectionTlm);
                break;
            case BUTTON_X_DOWN:
                //select motor4
                motor4Direction = motor4Direction.nextDirection();
                motor4DirectionTlm.setValue(motor4Direction);
                setDirection(motor4Direction, motor4, motor4DirectionTlm);
                break;
            case RIGHT_TRIGGER_DOWN:
                driveMotors(motor1, motor2, motor3, motor4);
                //drive motors
                break;
            case DPAD_UP_DOWN:
                motorPower = 1.0;
                break;
            case DPAD_DOWN_DOWN:
                motorPower = 0.5;
                break;
            case DPAD_LEFT_DOWN:
                //controlling distance we move the motor
                //value = encoder ticks
                motorPosDelta = 64;
                break;
            case DPAD_RIGHT_DOWN:
                //value = encoder ticks
                //controlling distance we move the motor
                motorPosDelta = 16;
                break;
        }
    }

}
