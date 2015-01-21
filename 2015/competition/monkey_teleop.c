#pragma config(Hubs,  S1, HTMotor,  HTMotor,  HTMotor,  HTServo)
#pragma config(Hubs,  S2, HTServo,  none,     none,     none)
#pragma config(Sensor, S1,     ,               sensorI2CMuxController)
#pragma config(Sensor, S2,     ,               sensorI2CMuxController)
#pragma config(Sensor, S3,     HTSMUX,         sensorI2CCustom)
#pragma config(Motor,  motorA,          rampRight,     tmotorNXT, PIDControl, reversed)
#pragma config(Motor,  motorB,          rampLeft,      tmotorNXT, PIDControl, encoder)
#pragma config(Motor,  motorC,           ,             tmotorNXT, openLoop)
#pragma config(Motor,  mtr_S1_C1_1,     driveFrontLeft, tmotorTetrix, PIDControl, encoder)
#pragma config(Motor,  mtr_S1_C1_2,     driveRearLeft, tmotorTetrix, PIDControl, encoder)
#pragma config(Motor,  mtr_S1_C2_1,     elbow,         tmotorTetrix, PIDControl, encoder)
#pragma config(Motor,  mtr_S1_C2_2,     conveyor,      tmotorTetrix, PIDControl, encoder)
#pragma config(Motor,  mtr_S1_C3_1,     driveFrontRight, tmotorTetrix, PIDControl, reversed, encoder)
#pragma config(Motor,  mtr_S1_C3_2,     driveRearRight, tmotorTetrix, PIDControl, reversed, encoder)
#pragma config(Servo,  srvo_S1_C4_1,    leftEye,              tServoStandard)
#pragma config(Servo,  srvo_S1_C4_2,    servo2,               tServoNone)
#pragma config(Servo,  srvo_S1_C4_3,    rightEye,             tServoStandard)
#pragma config(Servo,  srvo_S1_C4_4,    servo4,               tServoNone)
#pragma config(Servo,  srvo_S1_C4_5,    roller,               tServoStandard)
#pragma config(Servo,  srvo_S1_C4_6,    servo6,               tServoNone)
#pragma config(Servo,  srvo_S2_C1_1,    autoElbow,            tServoStandard)
#pragma config(Servo,  srvo_S2_C1_2,    servo8,               tServoNone)
#pragma config(Servo,  srvo_S2_C1_3,    autoThumb,            tServoStandard)
#pragma config(Servo,  srvo_S2_C1_4,    servo10,              tServoNone)
#pragma config(Servo,  srvo_S2_C1_5,    servo11,              tServoNone)
#pragma config(Servo,  srvo_S2_C1_6,    servo12,              tServoNone)
//*!!Code automatically generated by 'ROBOTC' configuration wizard               !!*//

#include "JoystickDriver.c"  //Include file to "handle" the Bluetooth messages.
#include "./../lib/sensors/drivers/hitechnic-sensormux.h"
#include "./../lib/sensors/drivers/lego-ultrasound.h"

// The sensor is connected to the first port
// of the SMUX which is connected to the NXT port S1.
// To access that sensor, we must use msensor_S1_1.  If the sensor
// were connected to 3rd port of the SMUX connected to the NXT port S4,
// we would use msensor_S4_3

const tMUXSensor LEGOUS = msensor_S3_1;

#define RAMP_SPEED_UP              -30
#define RAMP_SPEED_DOWN             30
#define SHOULDER_SPEED_UP           10
#define SHOULDER_SPEED_DOWN        -10

#define SERVO_ROLLER_UP             48
#define SERVO_ROLLER_DOWN           137

#define SERVO_AUTOELBOW_UP          37
#define SERVO_AUTOELBOW_DOWN        233

#define SERVO_ROLLER_OSC_UP         60
#define SERVO_ROLLER_OSC_DOWN       105

#define CONVEYOR_POWER              80
#define LSERVO_DOCK_FINGER_STOWED   82
#define LSERVO_DOCK_FINGER_UP       205
#define LSERVO_DOCK_FINGER_DOWN     161
#define RSERVO_DOCK_FINGER_STOWED   144
#define RSERVO_DOCK_FINGER_UP       0
#define RSERVO_DOCK_FINGER_DOWN     64

static int drive_multiplier = 1;

bool deadman_ltu_running;
bool deadman_ltd_running;
bool deadman_rtu_running;
bool deadman_rtd_running;


task oscillateRoller()
{
    int i;

    servo[roller] = SERVO_ROLLER_OSC_UP;
    wait1Msec(500);

    while (true) {
        for (i = SERVO_ROLLER_OSC_UP; i <= SERVO_ROLLER_OSC_DOWN; i++) {
            servo[roller] = i;
            wait1Msec(10);
        }

        for (i = SERVO_ROLLER_OSC_DOWN; i >= SERVO_ROLLER_OSC_UP; i--) {
            servo[roller] = i;
            wait1Msec(10);
        }
    }
}

typedef enum conveyor_state_ {
    CONVEYOR_OFF,
    CONVEYOR_FORWARD,
    CONVEYOR_BACKWARD,
} conveyor_state_t;

typedef enum shoulder_state_ {
    SHOULDER_STOP,
    SHOULDER_UP,
    SHOULDER_DOWN,
} shoulder_state_t;

typedef enum ramp_state_ {
    RAMP_UP,
    RAMP_DOWN,
    RAMP_STOP,
} ramp_state_t;

typedef enum joystick_event_ {
    RIGHT_TRIGGER_UP = 6,
    RIGHT_TRIGGER_DOWN = 8,
    LEFT_TRIGGER_UP = 5,
    LEFT_TRIGGER_DOWN = 7,
    BUTTON_ONE = 1,
    BUTTON_TWO = 2,
    BUTTON_THREE = 3,
    BUTTON_FOUR = 4,
    BUTTON_TEN	= 10,
} joystick_event_t;

conveyor_state_t conveyor_state;
ramp_state_t ramp_state;
shoulder_state_t shoulder_state;

bool debounce;

task debounceTask()
{
    debounce = true;
    wait1Msec(500);
    debounce = false;
}

task ball_watch()
{
    int dist;
    int i;

    while (true) {
        dist = USreadDist(LEGOUS);
        if (dist <= 7) {
            wait1Msec(500);
	        for (i = SERVO_ROLLER_OSC_UP; i <= SERVO_ROLLER_OSC_DOWN; i++) {
	            servo[roller] = i;
	            wait1Msec(10);
	        }

        }
    }
}

void shoulder_enter_state(shoulder_state_t state)
{
    shoulder_state = state;

    switch (state) {
    case SHOULDER_UP:
        motor[elbow] = SHOULDER_SPEED_UP;
        motor[elbow] = SHOULDER_SPEED_UP;
        break;
    case SHOULDER_DOWN:
        motor[elbow] = SHOULDER_SPEED_DOWN;
        motor[elbow] = SHOULDER_SPEED_DOWN;
        break;
    case SHOULDER_STOP:
        motor[elbow] = 0;
        motor[elbow] = 0;
        break;
    }
}

void ramp_enter_state(ramp_state_t state)
{
    ramp_state = state;

    switch (state) {
    case RAMP_UP:
        motor[motorA] = RAMP_SPEED_UP;
        motor[motorB] = RAMP_SPEED_UP;
        break;
    case RAMP_DOWN:
        motor[motorA] = RAMP_SPEED_DOWN;
        motor[motorB] = RAMP_SPEED_DOWN;
        break;
    case RAMP_STOP:
        motor[motorA] = 0;
        motor[motorB] = 0;
        break;
    }
}

task deadman_ltu()
{
    if (!deadman_ltd_running) {
	    deadman_ltu_running = true;
	    shoulder_enter_state(SHOULDER_UP);

	    while ((joy1Btn(Btn5)) && (deadman_ltd_running == false)) {
	    }

	    shoulder_enter_state(SHOULDER_STOP);
	    deadman_ltu_running = false;
    }
}

task deadman_ltd()
{
    if (!deadman_ltu_running) {
	    deadman_ltd_running = true;
	    shoulder_enter_state(SHOULDER_DOWN);

	    while ((joy1Btn(Btn7)) && (deadman_ltu_running == false)) {
	    }

	    shoulder_enter_state(SHOULDER_STOP);
	    deadman_ltd_running = false;
    }
}

task deadman_rtu()
{
    if (!deadman_rtd_running) {
	    deadman_rtu_running = true;
	    ramp_enter_state(RAMP_UP);

	    while ((joy1Btn(Btn6)) && (deadman_rtd_running == false)) {
	    }

	    ramp_enter_state(RAMP_STOP);
	    deadman_rtu_running = false;
    }
}

task deadman_rtd()
{
    if (!deadman_rtu_running) {
	    deadman_rtd_running = true;
	    ramp_enter_state(RAMP_DOWN);

	    while ((joy1Btn(Btn8)) && (deadman_rtu_running == false)) {
	    }

	    ramp_enter_state(RAMP_STOP);
	    deadman_rtd_running = false;
    }
}

void all_stop()
{
    motor[driveFrontLeft] = 0;
    motor[driveRearLeft] = 0;
    motor[driveFrontRight] = 0;
    motor[driveRearRight] = 0;
}


void conveyor_enter_state(conveyor_state_t state)
{
    conveyor_state = state;

    switch (state) {
    case CONVEYOR_OFF:
        motor[conveyor] = 0;
        break;
    case CONVEYOR_FORWARD:
        motor[conveyor] = CONVEYOR_POWER;
        break;
    case CONVEYOR_BACKWARD:
        motor[conveyor] = -CONVEYOR_POWER;
        break;
    }
}

task validate_conveyor()
{
    while (true) {
        conveyor_enter_state(CONVEYOR_FORWARD);
        wait1Msec(2000);
    }
}

void initializeRobot()
{
    deadman_ltu_running = false;
    deadman_ltd_running = false;
    deadman_rtu_running = false;
    deadman_rtd_running = false;

    conveyor_enter_state(CONVEYOR_OFF);
    ramp_enter_state(RAMP_STOP);

    servo[roller] = SERVO_ROLLER_DOWN;

    servo[autoElbow] = 233;

    all_stop();

    return;
}

void handle_joy1_btn1()
{
    switch (conveyor_state) {
    case CONVEYOR_FORWARD:
        conveyor_enter_state(CONVEYOR_OFF);
        break;
    case CONVEYOR_BACKWARD:
        conveyor_enter_state(CONVEYOR_FORWARD);
        break;
    case CONVEYOR_OFF:
        conveyor_enter_state(CONVEYOR_FORWARD);
        break;
    }
}

void handle_joy1_btn4()
{
    switch (conveyor_state) {
    case CONVEYOR_FORWARD:
        conveyor_enter_state(CONVEYOR_BACKWARD);
        break;
    case CONVEYOR_BACKWARD:
        conveyor_enter_state(CONVEYOR_OFF);
        break;
    case CONVEYOR_OFF:
        conveyor_enter_state(CONVEYOR_BACKWARD);
        break;
    }
}

void handle_joy1_btn10()
{
    servo[roller] = SERVO_ROLLER_UP;
}

void handle_joy1_btn3()
{
    servo[autoElbow] = SERVO_AUTOELBOW_UP;
}

void handle_joy1_rtu()
{
    if (!deadman_rtu_running) {
        startTask(deadman_rtu);
    }
}

void handle_joy1_rtd()
{
    if (!deadman_rtd_running) {
        startTask(deadman_rtd);
    }
}

void handle_joy1_ltu()
{
    if (!deadman_ltu_running) {
        startTask(deadman_ltu);
    }
}

void handle_joy1_ltd()
{
    if (!deadman_ltd_running) {
        startTask(deadman_ltd);
    }
}

void handle_joy1_event(joystick_event_t event)
{
    switch (event) {
    case BUTTON_ONE:
        handle_joy1_btn1();
        break;
    case BUTTON_THREE:
        handle_joy1_btn3();
        break;
    case BUTTON_FOUR:
        handle_joy1_btn4();
        break;
    case BUTTON_TEN:
		handle_joy1_btn10();
		break;
    case RIGHT_TRIGGER_UP:
        handle_joy1_rtu();
        break;
    case RIGHT_TRIGGER_DOWN:
        handle_joy1_rtd();
        break;
    case LEFT_TRIGGER_UP:
        handle_joy1_ltu();
        break;
    case LEFT_TRIGGER_DOWN:
        handle_joy1_ltd();
        break;
    }

    startTask(debounceTask);
}

task main()
{
    short right_y;
    short left_y;

    debounce = false;

    initializeRobot();

    waitForStart();   // wait for start of tele-op phase

    startTask(validate_conveyor);

    servo[roller] = SERVO_ROLLER_UP;
    // servo[autoElbow] = 37;

    //startTask(ball_watch);

    // StartTask(endGameTimer);

    while (true) {

        getJoystickSettings(joystick);

        if (!debounce) {
	        if (joy1Btn(Btn1)) {
	            handle_joy1_event(BUTTON_ONE);
	        } else if (joy1Btn(Btn4)) {
	            handle_joy1_event(BUTTON_FOUR);
	        } else if (joy1Btn(Btn10)) {
        		handle_joy1_event(BUTTON_TEN);
	        } else if (joy1Btn(Btn3)) {
        		handle_joy1_event(BUTTON_THREE);
	        } else if (joy1Btn(Btn5)) {
	            handle_joy1_event(LEFT_TRIGGER_UP);
	        } else if (joy1Btn(Btn7)) {
	            handle_joy1_event(LEFT_TRIGGER_DOWN);
	        } else if (joy1Btn(Btn6)) {
	            handle_joy1_event(RIGHT_TRIGGER_UP);
	        } else if (joy1Btn(Btn8)) {
	            handle_joy1_event(RIGHT_TRIGGER_DOWN);
	        } else if (joy1Btn(Btn6)) {
	        	motor[elbow]=100;
	        } else if (joy1Btn(Btn8)) {
	        	motor[elbow]=-100;
	        } else {
	        	motor[elbow]=0;
	        }
        }

        //if (drive_multiplier) {
            //right_y = joystick.joy1_y2;
            //left_y = joystick.joy1_y1;
        //} else {
            left_y = joystick.joy2_y1;
            right_y = joystick.joy2_y2;
        //}

        if (abs(right_y) > 10) {
	    	motor[driveFrontRight] = drive_multiplier * right_y * 0.80;
	    	motor[driveRearRight] = drive_multiplier * right_y * 0.80;
		} else {
		    motor[driveFrontRight] = 0;
		    motor[driveRearRight] = 0;
		}

        if (abs(left_y) > 10) {
		    motor[driveFrontLeft] = drive_multiplier * left_y * 0.80;
		    motor[driveRearLeft] = drive_multiplier * left_y * 0.80;
		} else {
		    motor[driveFrontLeft] = 0;
		    motor[driveRearLeft] = 0;
		}
    }
}
