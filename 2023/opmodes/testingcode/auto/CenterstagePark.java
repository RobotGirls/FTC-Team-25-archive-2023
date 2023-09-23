package opmodes.testingcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.openftc.apriltag.AprilTagDetection;

import team25core.DeadReckonPath;
import team25core.DeadReckonTask;
import team25core.FourWheelDirectDrivetrain;
import team25core.OneWheelDirectDrivetrain;
import team25core.Robot;
import team25core.RobotEvent;
import team25core.RunToEncoderValueTask;
import team25core.SingleShotTimerTask;
import team25core.vision.apriltags.AprilTagDetectionTask;


@Autonomous(name = "CenterstageParkAuto")
//@Disabled

public class CenterstagePark extends Robot {


    //wheels
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private FourWheelDirectDrivetrain drivetrain;


    //mechs
    private Servo claw;
    private Servo linkage;

    private DcMotor angledLiftOne;
    private DcMotor angledLiftTwo;
    private OneWheelDirectDrivetrain liftDriveTrainOne;
    private OneWheelDirectDrivetrain liftDriveTrainTwo;

    private OneWheelDirectDrivetrain hangingDrivetrain;
    private OneWheelDirectDrivetrain shootDroneDrivetrain;

    private DcMotor hanging;
    private DcMotor droneShooter;
    private DcMotor intake;

    //private OneWheelDirectDrivetrain turretDrivetrain;


    //sensors
    //private DistanceSensor alignerDistanceSensor;
    //private DistanceSensorCriteria distanceSensorCriteria;
    //private ColorSensor linearColorSensor;


    //paths
    private DeadReckonPath forwardPath;
    private DeadReckonPath rightPath;


    private DeadReckonPath liftMechOne;
    private DeadReckonPath liftMechTwo;
    private DeadReckonPath shootDronePath;
    private DeadReckonPath hangPath;

    private DeadReckonTask linearTaskOne;
    private DeadReckonTask linearTaskTwo;

    private DeadReckonTask hangTask;
    private DeadReckonTask droneTask;

    private DeadReckonPath goToJunctionPath;

    private DeadReckonPath deliverConePath;

    //variables for constants
    static final double FORWARD_DISTANCE = 13.5;
    static final double DRIVE_SPEED = 0.4;

    // apriltags detection
    private Telemetry.Item tagIdTlm;
    private Telemetry.Item parkingLocationTlm;
    AprilTagDetection tagObject;
    private AprilTagDetectionTask detectionTask;

    public static double EDITTHIS = 5;


    //telemetry
    private Telemetry.Item whereAmI;

    private RunToEncoderValueTask linearLiftTaskOne;
    private RunToEncoderValueTask linearLiftTaskTwo;

    private static final int DELAY = 5000;
    private String detect = "";

    public String delaySequence = "will drop cone";

    /*
     * The default event handler for the robot.
     */

    @Override
    public void handleEvent(RobotEvent e)
    {
        /*
         * Every time we complete a segment drop a note in the robot log.
         */
        if (e instanceof DeadReckonTask.DeadReckonEvent) {
            RobotLog.i("Completed path segment %d", ((DeadReckonTask.DeadReckonEvent)e).segment_num);
        }
    }



    public void setAprilTagDetection() {
        detectionTask = new AprilTagDetectionTask(this, "Webcam 1") {
            @Override
            public void handleEvent(RobotEvent e) {
                TagDetectionEvent event = (TagDetectionEvent) e;
                tagObject = event.tagObject;
                tagIdTlm.setValue(tagObject.id);
                whereAmI.setValue("in handleEvent");

                if (tagObject.id == 0) {
                    detect = "left";
                    liftOne();
                    addTask(linearTaskOne);
                    goToJunction();

                }
                if (tagObject.id == 6) {
                    detect = "right";
                    liftOne();
                    addTask(linearTaskOne);
                    goToJunction();
                }
                if (tagObject.id == 19) {
                    detect = "middle";
                    liftOne();
                    addTask(linearTaskOne);
                    goToJunction();
                }
                //addTask(linearLiftTask);




            }
        };
        whereAmI.setValue("setAprilTagDetection");
        detectionTask.init(telemetry, hardwareMap);
    }



    public void initPaths()
    {
        forwardPath = new DeadReckonPath();
        rightPath = new DeadReckonPath();

        forwardPath.stop();
        rightPath.stop();

        forwardPath.addSegment(DeadReckonPath.SegmentType.STRAIGHT,2.0,0.3);
        rightPath.addSegment(DeadReckonPath.SegmentType.SIDEWAYS,2.0,0.3);

    }

    @Override
    public void init()
    {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
/*
        claw=hardwareMap.servo.get("claw");
        linkage=hardwareMap.servo.get("linkage");
        */
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        drivetrain = new FourWheelDirectDrivetrain(frontRight, backRight, frontLeft, backLeft);
        drivetrain.resetEncoders();
        drivetrain.encodersOn();
/*
        whereAmI = telemetry.addData("location in code", "init");
        tagIdTlm = telemetry.addData("tagId","none");
        parkingLocationTlm = telemetry.addData("parking location: ","none");
*/
        /*
        angledLiftOne=hardwareMap.get(DcMotor.class, "linearLiftOne");
        angledLiftOne.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        angledLiftOne.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        angledLiftOne.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        liftDriveTrainOne = new OneWheelDirectDrivetrain(angledLiftOne);
        liftDriveTrainOne.resetEncoders();
        liftDriveTrainOne.encodersOn();

        angledLiftTwo=hardwareMap.get(DcMotor.class, "linearLiftTwo");
        angledLiftTwo.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        angledLiftTwo.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        angledLiftTwo.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        liftDriveTrainTwo = new OneWheelDirectDrivetrain(angledLiftTwo);
        liftDriveTrainTwo.resetEncoders();
        liftDriveTrainTwo.encodersOn();

        //open claw
        claw.setPosition(0.55);
        linkage.setPosition(0.55);
*/


        //linearColorSensor = hardwareMap.get(RevColorSensorV3.class, "liftColorSensor");
        //alignerDistanceSensor = hardwareMap.get(Rev2mDistanceSensor.class, "alignerDistanceSensor");
/*
        turret.setTargetPosition(0);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setPower(0.5);
*/

        initPaths();


    }




    //  lifting & dropping paths --------------------------------------

    public void  goToJunction()
    {

        this.addTask(new DeadReckonTask(this, goToJunctionPath,drivetrain ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {

                    delayAndDrop(3000);
                }
            }
        });
    }




    private void delayAndDrop(int delayInMsec) {
        this.addTask(new SingleShotTimerTask(this, delayInMsec) {
            @Override
            public void handleEvent(RobotEvent e) {
                SingleShotTimerEvent event = (SingleShotTimerEvent) e;
                if (event.kind == EventKind.EXPIRED ) {
                    whereAmI.setValue("in delay task");

                    dropPixel();

                }
            }
        });

    }
    private void dropPixel() {
        claw.setPosition(0);
        delayAndDrop2(1000);
        whereAmI.setValue("dropped the pixel");
    }

    private void delayAndDrop2(int delayInMsec) {
        this.addTask(new SingleShotTimerTask(this, delayInMsec) {
            @Override
            public void handleEvent(RobotEvent e) {
                SingleShotTimerEvent event = (SingleShotTimerEvent) e;
                if (event.kind == EventKind.EXPIRED ) {
                    whereAmI.setValue("in delay task");
                    //detectedBasedPathSelection();

                }
            }
        });

    }


/*
    private void detectedBasedPathSelection(){



        if ( detect == "right")
        {
            gotoRightPark();
        }
        if ( detect == "middle")
        {
            gotoMiddlePark();
        }
        if ( detect == "left")
        {
            gotoLeftPark();
        }

    }

*/


    // parking paths -----------------------------------

    public void goForward()
    {
        this.addTask(new DeadReckonTask(this, forwardPath,drivetrain ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    RobotLog.i("went forward");
                    //whereAmI.setValue("went to right target zone");
                    goRight();

                }
            }
        });
    }

    public void goRight()
    {
        this.addTask(new DeadReckonTask(this, rightPath,drivetrain ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    RobotLog.i("went right");
                    //delayAndDrop(DELAY);
                }
            }
        });
    }


    public void liftOne()
    {
        linearTaskOne = new DeadReckonTask(this, liftMechOne,liftDriveTrainOne ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    whereAmI.setValue("lifting lift one");

                }
            }
        };
    }

    public void liftTwo()
    {
        linearTaskTwo = new DeadReckonTask(this, liftMechTwo,liftDriveTrainTwo ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    whereAmI.setValue("lifting lift two");

                }
            }
        };
    }

    public void hang()
    {
        hangTask = new DeadReckonTask(this, hangPath,hangingDrivetrain ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    whereAmI.setValue("hanging robot");

                }
            }
        };
    }

    public void shootDrone()
    {
        droneTask = new DeadReckonTask(this, shootDronePath,shootDroneDrivetrain ){
            @Override
            public void handleEvent(RobotEvent e) {
                DeadReckonEvent path = (DeadReckonEvent) e;
                if (path.kind == EventKind.PATH_DONE)
                {
                    whereAmI.setValue("shooting drone");

                }
            }
        };
    }


    @Override
    public void start()
    {
        //whereAmI.setValue("in Start");
        //setAprilTagDetection();
        //addTask(detectionTask);
        goForward();
//
//        addTask(linearLiftTask);
//        goToJunction();
//
//        goliftMech();




    }
}