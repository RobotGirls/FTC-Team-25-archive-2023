package TestRig;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import team25core.GamepadTask;
import team25core.Robot;

public class SelectModes {
    public enum TestRigMode {
        INDEP_MOTOR,
        SERVO,
        DRIVE,
        SERVO_N_DRIVE;

        public TestRigMode nextMode() {
            TestRigMode currentMode = values()[ordinal()];

            if (currentMode != SERVO_N_DRIVE) {
                return values()[ordinal() +1];
            } else {
                return values()[0];
            }
        }
    }

    private Robot myRobot;
    private TestRigMode mode;
    private Telemetry.Item msgTelem;
    private Telemetry.Item modeTelem;
    private GamepadTask myGamepad1Task;


    //constructor, used to instantiate a SelectModes object and allocate it memory
    SelectModes(Robot theirRobot) {
        myRobot = theirRobot;
        mode = TestRigMode.INDEP_MOTOR;
        msgTelem = myRobot.telemetry.addData("Use right trigger to toggle through modes", "");
        modeTelem = myRobot.telemetry.addData("Current mode: ", mode);
    }


    //sets Gamepad1 up for mode selection, type void so it returns nothing
    //modifier is protected
    protected void setUpGamepad1ForModeSelection(GamepadTask.GamepadEvent event) {

        // casting instance of myRobot(generic robot type) into TestRigDemo type
        TestRigDemo demoInstance = (TestRigDemo) myRobot;

        switch (event.kind) {
            case RIGHT_TRIGGER_DOWN:
                mode = mode.nextMode();
                // prints the next mode to the driver station phone
                modeTelem.setValue(mode);
                break;
            case BUTTON_X_DOWN:
                demoInstance.setTestRigMode(mode);
                msgTelem.setValue("Completed mode selection");
                //Optional remove gamepad task after completed selection
                //myRobot.removeTask(myGamepad1Task);
                break;
            default:
                msgTelem.setValue("Use right trigger to select mode, use button X to exit mode selection");
        }
        return;
    }
}
