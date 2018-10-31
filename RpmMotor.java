//--------------------------------------------------------------------
//RpmMotor.java -- Test the RPM motor
//
//Created 2018-10-19  DLB
//--------------------------------------------------------------------

package org.usfirst.frc.team4415.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.ErrorCode;
import ecommon.RobotMap;
import ecommon.ButtonToggle;
import ecommon.ReportTalon;

public class RpmMotor {
	private Joystick m_joystick;
	private TalonSRX m_motor_rpm = new TalonSRX(RobotMap.RPMTestingMotor);
	private ButtonToggle m_mode_btn;
	
	private int m_motor_mode = 0;  // 0=manual  1=750 RPM  2=1500 RPM
	private double m_rpm = 0.0;    // Current RPM.
	private double m_demand = 0.0; // Current Demand.
	
	public void Initialize(Joystick j) {
		m_joystick = j;
		m_mode_btn = new ButtonToggle(m_joystick, 2);
		ErrorCode err;
		err = m_motor_rpm.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);
		if (err != ErrorCode.OK) {
			System.out.printf("Err (%d: %s) on SelectFeedbackSensor (#1)\n", err.value, err.toString());
		}
		double secsFromNeutralToFull = 0.25;
		err = m_motor_rpm.configClosedloopRamp(secsFromNeutralToFull, 10);
		if (err != ErrorCode.OK) {
			System.out.printf("Err (%d: %s) on configClosedloopRamp (#2)\n", err.value, err.toString());
		}
		m_motor_rpm.setSensorPhase(true);
	}

	public void Report() {
		ReportTalon.Report("Ex1", m_motor_rpm);
		SmartDashboard.putNumber("Motor_Mode",  m_motor_mode);
		SmartDashboard.putNumber("DesiredRPM",  m_rpm);
		SmartDashboard.putNumber("Demand",  m_demand);
		SmartDashboard.putNumber("SensorPos0",  m_motor_rpm.getSelectedSensorPosition(0));
		SmartDashboard.putNumber("SensorVel0",  m_motor_rpm.getSelectedSensorVelocity(0));
		double actual_rps = m_motor_rpm.getSelectedSensorVelocity(0) * 10.0 / 1024.0;
		SmartDashboard.putNumber("MeasuredRPM", actual_rps * 60.0);
		SmartDashboard.putNumber("LoopErr", m_motor_rpm.getClosedLoopError(0));
	}
	
	public void Disable() {
		m_motor_mode = 0;
		m_rpm = 0;
		m_demand = 0;
		m_motor_rpm.set(ControlMode.PercentOutput,  0.0);
	}

	// Calculate "demand" which is the encoder counts that should happen
	// during a 100ms period.
	private double calculateDemand(double rpm) {
		double counts_per_rev = 1024.0;  // By experiment with SmartDashboard and Encoder.
		double counts_per_rpm = rpm * counts_per_rev;
		double counts_per_sec = counts_per_rpm / 60.0;
		double counts_per_demand_period = counts_per_sec * 0.100;  
		return counts_per_demand_period;
	}
	
	public void StartPeriodic() {
		m_motor_mode = 0;
		m_rpm = 0;
		m_demand = 0;
		m_mode_btn.Reset();
		m_motor_rpm.set(ControlMode.PercentOutput, 0.0);	
	}
	
	public void RunPeriodic() {
		if (m_mode_btn.CheckTrigger()) {
			// Count the mode.
			m_motor_mode++;
			if (m_motor_mode > 4) {
				m_motor_mode = 0;
				m_motor_rpm.set(ControlMode.PercentOutput,  0.0);
				return;
			}
			if (m_motor_mode >= 1 && m_motor_mode <= 4 ) {
				m_rpm = 750 * m_motor_mode;
				m_demand = calculateDemand(m_rpm);
				m_motor_rpm.set(ControlMode.Velocity, m_demand);
		     	return;
			}
		}
		if (m_motor_mode == 0) {
			m_rpm = 0.0;
			double xval = m_joystick.getX();
			double yval = m_joystick.getY();
			SmartDashboard.putNumber("Joystick-X:",  xval);
			SmartDashboard.putNumber("Joystick-Y:",  yval);
			m_motor_rpm.set(ControlMode.PercentOutput, yval/4.0);
		}
	}
}