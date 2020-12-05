package test;

import team25core.Robot;
import team25core.RobotTask;

public class RingDetectionTest extends RobotTask {

    public RingDetectionTest(Robot robot) {
        super(robot);
    }

    @Override
    public boolean timeslice() {
        return false;
    }
}
