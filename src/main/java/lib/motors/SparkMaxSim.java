package lib.motors;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.math.system.LinearSystem;
import edu.wpi.first.math.system.plant.DCMotor;

public class SparkMaxSim extends SimMotor {

    public SparkMaxSim(int numMotors, double gearing, double jKgMetersSquared) {
        super(DCMotor.getNEO(numMotors), jKgMetersSquared, gearing);
    }

    public SparkMaxSim(LinearSystem<N2, N1, N2> model, int numMotors, double gearing) {
        super(model, DCMotor.getNEO(numMotors), gearing);
    }

    public void set(double speed) {
        setInputVoltage(speed * 12.0);
    }

    public void setInputVoltage(double voltage) {
        voltageRequest = voltage;
        motorSim.setInputVoltage(voltageRequest);
    }

    public void setReference(double value, CANSparkMax.ControlType ctrl) {
        setReference(value, ctrl, 0);
    }

    public void setReference(double value, CANSparkMax.ControlType ctrl, double arbFeedforward) {
        switch (ctrl) {
            case kDutyCycle:
                set(value);
                break;
            case kPosition:
                setInputVoltage(controller.calculate(getPosition(), value) + arbFeedforward);
                break;
            case kSmartMotion:
                setInputVoltage(profiledController.calculate(getPosition(), value) + arbFeedforward);
                break;
            case kVelocity:
                setInputVoltage(controller.calculate(getVelocity(), value) + arbFeedforward);
                break;
            case kSmartVelocity:
                setInputVoltage(profiledController.calculate(getVelocity(), value) + arbFeedforward);
                break;
            case kVoltage:
                setInputVoltage(value);
                break;
            case kCurrent:
                System.out.println("Can't use current control for spark max in sim!");
                break;
        }
    }

    public double getBusVoltage() {
        return voltageRequest;
    }

    public double getAppliedOutput() {
        return voltageRequest / 12.0;
    }

    public double getVelocity() {
        return motorSim.getAngularVelocityRPM();
    }

    public double getPosition() {
        return motorSim.getAngularPositionRotations();
    }

    public double getOutputCurrent() {
        return motorSim.getCurrentDrawAmps();
    }
}
