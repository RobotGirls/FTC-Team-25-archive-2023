package TestRig;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import team25core.GamepadTask;
import team25core.Robot;
import team25core.RobotEvent;

@TeleOp(name = "Test Rig Demo")
//@Disabled

public class TestRigDemo extends Robot {
    // enum selects which configuration we are currently using on the phone
    public enum GamepadControlChoice {
        DRIVE_CONTROL,
        INDEP_MOTOR_CONTROL,
        SELECT_TESTRIG_MODE,
        SERVO_CONTROL,
        SERVO_N_DRIVE_CONTROL;
    }

    private GamepadTask gamepad1Task;
    private SelectModes modes;
    private IndepMotorMode indepMode;
    private SelectModes.TestRigMode selectedTestRigMode;
    // initializing gamepadControlChoice (of type GamepadControlChoice) to GamepadControlChoice.SELECT_TESTRIG_MODE;
    private GamepadControlChoice gamepadControlChoice = GamepadControlChoice.INDEP_MOTOR_CONTROL;
    private SelectModes.TestRigMode selectedMode;

    // this init method is called when the init button is pressed on the driver station
    @Override
    public void init() {
        // here we instantiate the SelectModes - set aside memory for that class
        modes = new SelectModes(this);
        indepMode = new IndepMotorMode(this);

        // here we instantiate the GamepadTask - set aside memory for that class
        gamepad1Task = new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_1);
        // begins monitoring the gamepad controls and generates gamepad events when the controls are pressed
        addTask(gamepad1Task);

    }

    public void setTestRigMode(SelectModes.TestRigMode modeFromSelectModes) {
        selectedTestRigMode = modeFromSelectModes;

        switch (selectedTestRigMode) {
            case INDEP_MOTOR:
                gamepadControlChoice = GamepadControlChoice.INDEP_MOTOR_CONTROL;
                break;
            case SERVO:
                gamepadControlChoice = GamepadControlChoice.SERVO_CONTROL;
                break;
            case DRIVE:
                gamepadControlChoice = GamepadControlChoice.DRIVE_CONTROL;
                break;
            case SERVO_N_DRIVE:
                gamepadControlChoice = GamepadControlChoice.SERVO_N_DRIVE_CONTROL;
                break;
            default:
                // Print useful error message
        }


    }

    // the handleEvent method is called after the task is added and a control is pressed
    @Override
    public void handleEvent(RobotEvent e) {
        // e (robot event) cast to variable of type GamepadTask.GamepadEvent
        if (e instanceof GamepadTask.GamepadEvent) {
            GamepadTask.GamepadEvent event = (GamepadTask.GamepadEvent) e;

            switch (gamepadControlChoice) {
                case SELECT_TESTRIG_MODE:
                    // calls the setUpGamepad1ForModeSelection method that is defined in the SelectModes class
                    modes.setUpGamepad1ForModeSelection(event);
                    break;
                case INDEP_MOTOR_CONTROL:
                    indepMode.setUpGamepadForMotorControl(event);
                    break;
                case SERVO_CONTROL:
                    break;
                case DRIVE_CONTROL:
                    break;
                case SERVO_N_DRIVE_CONTROL:
                    break;
                default:
                    // print to driver station
            }
        }
    }

    @Override
    public void start() {

        if (selectedTestRigMode == SelectModes.TestRigMode.INDEP_MOTOR) {
            // do independent motor stuff
        }
        if (selectedTestRigMode == SelectModes.TestRigMode.SERVO) {
            // do servo stuff
        }
        if (selectedTestRigMode == SelectModes.TestRigMode.DRIVE) {
            // do motor stuff
        }
        if (selectedTestRigMode == SelectModes.TestRigMode.SERVO_N_DRIVE) {
            // do servo and drive stuff
        }
    }
}

