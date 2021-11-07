//package opmodes;
//
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.util.RobotLog;
//
//import org.firstinspires.ftc.robotcore.external.Telemetry;
//
//import team25core.DeadReckonPath;
//import team25core.DeadReckonTask;
//import team25core.GamepadTask;
//import team25core.MechanumGearedDrivetrain;
//import team25core.OneWheelDirectDrivetrain;
//import team25core.RingDetectionTask;
//import team25core.Robot;
//import team25core.RobotEvent;
//import team25core.SingleShotTimerTask;
//import team25core.StandardFourMotorRobot;
//import team25core.RingImageInfo;
//
//
//@Autonomous(name = " frenzy detection test2", group = "Team 25")
//// @Disabled
//public class FrenzyDetection extends Robot {
//
//    private final static String TAG = "CHO";
//    private final static int RING_TIMER = 5000;
//    private final double STRAIGHT_SPEED = 0.5;
//    private final double TURN_SPEED = 0.25;
//    private MechanumGearedDrivetrain drivetrain1;
//    private Telemetry.Item loggingTlm;
//    private Telemetry.Item objectSeenTlm;
//
//    private OneWheelDirectDrivetrain single;
//
//    private DcMotor frontLeft;
//    private DcMotor frontRight;
//    private DcMotor rearLeft;
//    private DcMotor rearRight;
//
//
//    private Telemetry.Item currentLocationTlm;
//    private Telemetry.Item handleEventTlm;
//    private int numTimesInHandleEvent = 0;
//    private double ringConfidence;
//    // private String ringType = "unknown";
//    private String ringType;
//
//    private DeadReckonPath launchLinePath;
//
//
//    DeadReckonPath path = new DeadReckonPath();
//
//    // declaring gamepad variables
//    //variables declarations have lowercase then uppercase
//    private GamepadTask gamepad;
//
//    RingDetectionTask rdTask;
//    RingImageInfo ringImageInfo;
//    SingleShotTimerTask rtTask;
//
//    @Override
//    public void handleEvent(RobotEvent e)
//    {
//        if (e instanceof DeadReckonTask.DeadReckonEvent) {
//            RobotLog.i("Completed path segment %d", ((DeadReckonTask.DeadReckonEvent) e).segment_num);
//        }
//    }
//
//
//
//    public void goToTargetZone(DeadReckonPath zonePath, final String whichZone)
//    {
//
//        this.addTask(new DeadReckonTask(this, zonePath, drivetrain1){
//            @Override
//            public void handleEvent(RobotEvent e) {
//                DeadReckonEvent path = (DeadReckonEvent) e;
//                if (path.kind == EventKind.PATH_DONE)
//                {
//                    RobotLog.i("reached target zone");
//                    currentLocationTlm.setValue("in goToTargetZone " + whichZone);
//
//                }
//            }
//        });
//    }
//
//
//
//
//    //need to fix 0 ring detection
//    public void startRingTimer() {
//        rtTask = new SingleShotTimerTask(this, RING_TIMER) {
//            //the handleEvent method is called when timer expires
//            @Override
//            public void handleEvent(RobotEvent e) {
//                SingleShotTimerTask.SingleShotTimerEvent event = (SingleShotTimerEvent) e;
//
//                if (event.kind == EventKind.EXPIRED) {
//                    objectSeenTlm.setValue("no rings");
//                    //if timer expires then no rings detected
//                    //stops ringDetectionTask
//                    rdTask.stop();
//                    currentLocationTlm.setValue("in SingleShotTimerTask handleEvent no ring");
//                    //goToTargetZone(targetZoneAPath, "zone A" );
//                }
//
//            }
//        };
//    }
//
//
//    public void loop()
//    {
//        super.loop();
//    }
//
//
//    public void initPath()
//    {
//        launchLinePath = new DeadReckonPath();
//
//        launchLinePath.stop();
//
//        launchLinePath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 70, -STRAIGHT_SPEED);
//
////        targetZoneAPath.addSegment(DeadReckonPath.SegmentType.TURN, 30, TURN_SPEED);
////        targetZoneAPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 75, -STRAIGHT_SPEED);
////
////        targetZoneBPath.addSegment(DeadReckonPath.SegmentType.TURN,10, TURN_SPEED);
////        targetZoneBPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 90,-STRAIGHT_SPEED);
////
////        targetZoneCPath.addSegment(DeadReckonPath.SegmentType.TURN,20, TURN_SPEED);
////        targetZoneCPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT,105, -STRAIGHT_SPEED);
//
//    }
//
//    public void setRingDetection()
//    {
//
//        rdTask = new RingDetectionTask(this, "Webcam1") {
//            //the handleEvent method is called when a ring is detected
//            @Override
//            public void handleEvent(RobotEvent e) {
//                RingDetectionTask.RingDetectionEvent event = (RingDetectionEvent) e;
//                ringImageInfo.getImageInfo(event);
//                ringConfidence = ringImageInfo.getConfidence();
//                ringType = ringImageInfo.getRingType();
//                currentLocationTlm.setValue("in RingDetectionTask handleEvent");
//                numTimesInHandleEvent++;
//                handleEventTlm.setValue(numTimesInHandleEvent);
//
//
//                if (event.kind == EventKind.OBJECTS_DETECTED) {
//                    objectSeenTlm.setValue(ringType);
//                    //  rdTask.stop();
//                    if (ringType.equals("Single") ){
//                        objectSeenTlm.setValue("single ring");
//                        currentLocationTlm.setValue("in RingDetectionTask handleEvent single ring");
//                        //    goToTargetZone(targetZoneBPath, "zone B" );
//                    } else if (ringType.equals("Quad")){
//                        objectSeenTlm.setValue("quad rings");
//                        currentLocationTlm.setValue("in RingDetectionTask handleEvent quad ring");
//                        //    goToTargetZone(targetZoneCPath, "zone C" );
//                    } else {
//                        objectSeenTlm.setValue("no rings");
//                    }
//                    //stops ring detection task
//                    //  rdTask.stop();
//                    //stops timer
//                    //  rtTask.stop();
//                }
//            }
//        };
//        currentLocationTlm.setValue("in setRingDetection");
//        rdTask.init(telemetry, hardwareMap);
////      //FIXME update quad ring detection to look for single ring or quad ring
//        rdTask.setDetectionKind(RingDetectionTask.DetectionKind.EVERYTHING);
//
//    }
//
//    @Override
//    public void init()
//    {
//        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
//        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
//        rearLeft = hardwareMap.get(DcMotor.class, "rearLeft");
//        rearRight = hardwareMap.get(DcMotor.class, "rearRight");
//
//
////        single     = new OneWheelDirectDrivetrain(wobbleLift);
////        single.resetEncoders();
////        single.encodersOn();
//
//        //caption: what appears on the phone
//        loggingTlm = telemetry.addData("distance traveled", "unknown");
//        currentLocationTlm = telemetry.addData("current location", "in init" );
//        handleEventTlm = telemetry.addData("num times in handle event", "0");
//        objectSeenTlm = telemetry.addData("saw", "unknown");
//
//        //initializing drivetrain
//        drivetrain1 = new MechanumGearedDrivetrain(frontRight, rearRight, frontLeft, rearLeft);
//        drivetrain1.resetEncoders();
//        drivetrain1.encodersOn();
//        RobotLog.i("start moving");
//
//        //initializing gamepad variables
//        gamepad = new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_1);
//        addTask(gamepad);
//
//        //instantiate ringImageInfo class and display telemetry
//        ringImageInfo = new RingImageInfo();
//        ringImageInfo.displayTelemetry(this.telemetry);
//
//        //starting ring detection
//        setRingDetection();
//        //  startRingTimer();
//
//        //initializing autonomous path
//        initPath();
//    }
//
//    @Override
//    public void start()
//    {
//        loggingTlm = telemetry.addData("log", "unknown");
//
//        currentLocationTlm.setValue("in start");
//        //starting ring detection task
//        addTask(rdTask);
//
//        //starting ring timer task
//        //addTask(rtTask);
//
//    }
//}