//package opmodes;
//
//import com.qualcomm.hardware.bosch.BNO055IMU;
//import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
//import com.qualcomm.hardware.rev.RevColorSensorV3;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.ColorSensor;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DistanceSensor;
//import com.qualcomm.robotcore.hardware.Servo;
//
//import org.firstinspires.ftc.robotcore.external.Telemetry;
//import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
//
//import team25core.DeadReckonPath;
//import team25core.DeadReckonTask;
//import team25core.DistanceSensorCriteria;
//import team25core.GamepadTask;
//import team25core.MechanumGearedDrivetrain;
//import team25core.OneWheelDirectDrivetrain;
//import team25core.OneWheelDriveTask;
//import team25core.RobotEvent;
//import team25core.RunToEncoderValueTask;
//import team25core.StandardFourMotorRobot;
//import team25core.TeleopDriveTask;
//import team25core.TwoStickMechanumControlScheme;
//
//import team25core.RGBColorSensorTask;
//import team25core.SingleShotColorSensorTask;
//import team25core.RGBColorSensorMotorTask;
//
//@TeleOp(name = "LM2TELEOP2wColorSensor")
////@Disabled
//public class TeleopLM2w2Stick2wColorSensor extends StandardFourMotorRobot {
//
//
//    private TeleopDriveTask drivetask;
//
//    private enum Direction {
//        CLOCKWISE,
//        COUNTERCLOCKWISE,
//    }
//
//    private BNO055IMU imu;
//
//    //duck carousel
//
//
//
//    private Telemetry.Item locationTlm;
//    private Telemetry.Item targetPositionTlm;
//
//
//
//    //TankMechanumControlSchemeFrenzy scheme;
//
//    TwoStickMechanumControlScheme scheme;
//
//    // private RunToEncoderValueTask turrtTask;
//
//    private MechanumGearedDrivetrain drivetrain;
//
//    private DcMotor linearLift;
//    private Servo umbrella;
//    private DcMotor turret;
//    private DeadReckonPath turretTurnOrangePath;
//    private DeadReckonPath turretTurnBluePath;
//    private OneWheelDirectDrivetrain turretDrivetrain;
//
//    private DistanceSensor alignerDistanceSensor;
//    private DistanceSensorCriteria distanceSensorCriteria;
//    private ColorSensor linearColorSensor;
//    private Telemetry.Item buttonTlm;
//    private Telemetry.Item colorDetectedTlm;
//    private Telemetry.Item redDetectedTlm;
//    private Telemetry.Item blueDetectedTlm;
//    private Telemetry.Item greenDetectedTlm;
//    protected RGBColorSensorTask colorSensorTask;
//    private ColorSensor colorSensor;
//
//    private final double ORANGE_DISTANCE = 5;
//    private final double BLUE_DISTANCE = 5;
//    private final double turretPower = 0.5;
//
//    private RunToEncoderValueTask turretTask;
//
//    private OneWheelDriveTask liftMotorTask;
//
//    //what does CR stand for
//
//    protected RGBColorSensorMotorTask rgbColorSensorMotorTask;
//    protected RGBColorSensorTask rgbColorSensorTask;
//
//
//    @Override
//    public void handleEvent(RobotEvent e) {
//    }
//
//    //flipover positions for bottom and top positions for intake and placing on hubs
//
//
//    //alternates between down and up positions.
//
//
//
//    @Override
//    public void init() {
//
//        super.init();
//        initIMU();
//
//        linearLift=hardwareMap.get(DcMotor.class, "linearLift");
//        linearLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        linearLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        linearLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//
//        turret = hardwareMap.get(DcMotor.class, "turret");
//        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//
//
//
//        turretDrivetrain = new OneWheelDirectDrivetrain(turret);
//        turretDrivetrain.resetEncoders();
//        turretDrivetrain.encodersOn();
//
//
//        umbrella = hardwareMap.servo.get("umbrella");
//
//        int initialturretPos = 5;
//        //turrtTask = new RunToEncoderValueTask(this,turret,initialturretPos,turretPower);
//
//
//
//        linearColorSensor = hardwareMap.get(RevColorSensorV3.class, "liftColorSensor");
//
//        alignerDistanceSensor = hardwareMap.get(Rev2mDistanceSensor.class, "alignerDistanceSensor");
//
//
//        //distanceSensorCriteria = DistanceSensorCriteria(alignerDistanceSensor,2);
//
//        // scheme = new TankMechanumControlSchemeFrenzy(gamepad1);
//        //scheme = new TankMechanumControlScheme(gamepad1);
//        scheme = new TwoStickMechanumControlScheme(gamepad1);
//
//
//        //code for forward mechanum drivetrain:
//        drivetrain = new MechanumGearedDrivetrain(motorMap);
//        drivetask = new TeleopDriveTask(this, scheme, frontLeft, frontRight, backLeft, backRight);
//        drivetask.slowDown(true);
//
//        locationTlm = telemetry.addData("location","init");
//        targetPositionTlm = telemetry.addData("target Pos","init");
//
//
//        turret.setTargetPosition(0);
//        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        turret.setPower(0.5);
//
//        liftMotorTask = new OneWheelDriveTask(this, linearLift, true);
//        liftMotorTask.slowDown(false);
//
//
//        initPaths();
//
//        buttonTlm = telemetry.addData("buttonState", "unknown");
//        colorDetectedTlm = telemetry.addData("color detected", "unknown");
//
//        blueDetectedTlm = telemetry.addData("blue", 0);
//        redDetectedTlm = telemetry.addData("red", 0);
//        greenDetectedTlm = telemetry.addData("green", 0);
//
//        rgbColorSensorMotorTask = new RGBColorSensorMotorTask(this, colorSensor, linearLift);
//
//        // set thresholds and target encoder values for each color
//        //rgbColorSensorMotorTask.setThresholds();
//        rgbColorSensorMotorTask.setTargetEncoderValues(5500, 4830, 3150);
//
//    }
//
//    private void initPaths() {
//        turretTurnBluePath = new DeadReckonPath();
//        turretTurnBluePath.stop();
//        turretTurnBluePath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, BLUE_DISTANCE, -0.4);
//
//        turretTurnOrangePath = new DeadReckonPath();
//        turretTurnOrangePath.stop();
//        turretTurnOrangePath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, ORANGE_DISTANCE, 0.4);
//    }
//
//    private void setTurretTurn(DeadReckonPath blueOrangePath) {
//        this.addTask(new DeadReckonTask(this, blueOrangePath, turretDrivetrain) {
//            @Override
//            public void handleEvent(RobotEvent e) {
//                DeadReckonEvent path = (DeadReckonEvent) e;
//                if (path.kind == EventKind.PATH_DONE) {
//                    //RobotLog.i("Finished turret path");
//                    //locationTlm.setValue("finished path");
//
//
//                }
//            }
//        });
//    }
//
//
//    public void initIMU()
//    {
//        // Retrieve the IMU from the hardware map
//        imu = hardwareMap.get(BNO055IMU.class, "imu");
//        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
//        // Technically this is the default, however specifying it is clearer
//        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
//        // Without this, data retrieving from the IMU throws an exception
//        imu.initialize(parameters);
//
//    }
//
//
//
//    @Override
//    public void start() {
//
//        this.addTask(drivetask);
//        // addTask(turrtTask);
//        locationTlm.setValue("in start");
//
//        this.addTask(new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_1) {
//            public void handleEvent(RobotEvent e) {
//                GamepadEvent gamepadEvent = (GamepadEvent) e;
//                locationTlm.setValue("in gamepad1 handler");
//                switch (gamepadEvent.kind) {
//                    case BUTTON_X_DOWN:
//                        locationTlm.setValue( "BLUE: " + linearColorSensor.blue() );
//                        break;
//                    case BUTTON_Y_DOWN:
//                        locationTlm.setValue( "RED: " + linearColorSensor.red() );
//                        break;
//                    case BUTTON_B_DOWN:
//                        locationTlm.setValue( "GREEN: " + linearColorSensor.green() );
//                        break;
//
//
//                }
//            }
//
//        });
//
//        this.addTask(liftMotorTask);
//
//        colorSensorTask = new RGBColorSensorTask(this, colorSensor) {
//            public void handleEvent(RobotEvent e) {
//                RGBColorSensorTask.ColorSensorEvent event = (RGBColorSensorTask.ColorSensorEvent) e;
//                // sets threshold for blue, red, and green to ten thousand
//                colorSensorTask.setThresholds(10000, 10000, 5000);
//                colorArray = colorSensorTask.getColors();
//                // shows the values of blue, red, green on the telemetry
//                blueDetectedTlm.setValue(colorArray[0]);
//                redDetectedTlm.setValue(colorArray[1]);
//                greenDetectedTlm.setValue(colorArray[2]);
//                switch(event.kind) {
//                    // red is at the end
//                    case RED_DETECTED:
//                        //  colorSensorTask.suspend();
//                        //  hLift.setPower(0.0);
//                        //  state = LiftStates.DROPPING;
//                        colorDetectedTlm.setValue("red");
//                        break;
//                    case BLUE_DETECTED:
//                        //  hLift.setPower(0.0);
//                        //  this.removeTask(colorSensorTask);
//                        colorDetectedTlm.setValue("blue");
//                        //  state = LiftStates.LOWERING;
//                        break;
//                    case GREEN_DETECTED:
//                        //  hLift.setPower(0.0);
//                        //  this.removeTask(colorSensorTask);
//                        colorDetectedTlm.setValue("green");
//                        //  state = LiftStates.LOWERING;
//                        break;
//                    default:
//                        colorDetectedTlm.setValue("none");
//                        break;
//                }
//            }
//        };
//        this.addTask(colorSensorTask);
//
//
//        //gamepad2 w /nowheels only mechs
//        this.addTask(new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_2) {
//            public void handleEvent(RobotEvent e) {
//                GamepadEvent gamepadEvent = (GamepadEvent) e;
//                locationTlm.setValue("in gamepad2 handler");
//                switch (gamepadEvent.kind) {
//                    case LEFT_BUMPER_DOWN:
//                        double distance = alignerDistanceSensor.getDistance(DistanceUnit.CM);
//                        locationTlm.setValue( "distance: " + distance); //=3.3-3.4
//                        break;
//                    case RIGHT_BUMPER_DOWN:
//                        linearLift.setPower(-1);
//                        break;
//                    case LEFT_BUMPER_UP:
//                        linearLift.setPower(0);
//                        break;
//                    case RIGHT_BUMPER_UP:
//                        linearLift.setPower(0);
//                        break;
//                    case RIGHT_TRIGGER_DOWN:
//                        umbrella.setPosition(0.55);
////                        if ( umbrella.getPosition() == 0.55)
////                        {
////                            turret.setTargetPosition(0);
////                            turret.setPower(0.5);
////                        }
//                        break;
//                    case LEFT_TRIGGER_DOWN:
//                        umbrella.setPosition(0);
//                        break;
//                    case DPAD_UP_DOWN:
//                        linearLift.setPower(-1);
//                        break;
//                    case DPAD_DOWN_DOWN:
//                        linearLift.setPower(0.5);
//                        break;
//                    case DPAD_UP_UP:
//                        linearLift.setPower(0);
//                        break;
//                    case DPAD_DOWN_UP:
//                        linearLift.setPower(0);
//                        break;
//                    case BUTTON_B_DOWN:
//                        turret.setTargetPosition(-485);
//                        turret.setPower(0.5);
//                        locationTlm.setValue(turret.getCurrentPosition());
//                        targetPositionTlm.setValue(turret.getTargetPosition());
//                        break;
//                    case BUTTON_X_DOWN:
//                        turret.setTargetPosition(475);
//                        turret.setPower(0.5);
//                        locationTlm.setValue(turret.getCurrentPosition());
//                        targetPositionTlm.setValue(turret.getTargetPosition());
//                        break;
//                    case BUTTON_Y_DOWN:
//                        turret.setTargetPosition(0);
//                        turret.setPower(0.5);
//                        locationTlm.setValue(turret.getCurrentPosition());
//                        targetPositionTlm.setValue(turret.getTargetPosition());
//                        break;
//                    case BUTTON_X_UP:
//                        turret.setPower(0);
//                        break;
//                    case BUTTON_B_UP:
//                        turret.setPower(0);
//                        break;
//                    case BUTTON_Y_UP:
//                        turret.setPower(0);
//                        break;
//
//
//
//
//
//                }
//            }
//
//        });
//
//
//    }
//}