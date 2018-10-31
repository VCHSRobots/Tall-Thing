// --------------------------------------------------------------------
// TallThing.java -- Controller for "Tall Thing"
// 
// Created 2018-10-30 DLB
// --------------------------------------------------------------------

package org.usfirst.frc.team4415.robot;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import ecommon.RobotMap;
import ecommon.ReportTalon;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TallThing {
	private TalonSRX m_motor_tall = new TalonSRX(RobotMap.TallMotor);
	private Joystick m_joystick;
	private double m_percent_out;
	public enum Mode {
		DISABLED, MANUAL, SEEK0
	}
	private Mode m_mode;
	
	// These are the button definations
	private int cButtonSeek = 1;
	private int cButtonSetPos = 4; 
	private int cButtonManualUp = 5;
	private int cButtonManualDown = 6;

	
	public void Initialize(Joystick j) {
		m_joystick = j;
		m_percent_out = 0;
		m_mode = Mode.DISABLED;
		setMotor(m_percent_out);
	}
	
	public void Report() {
		SmartDashboard.putString("Tall Mode",  m_mode.toString());
		ReportTalon.Report("Tall",  m_motor_tall);
		SmartDashboard.putNumber("Tall Out", m_percent_out);
	}
	
	public void Disable() {
		setMotor(0.0);
		m_mode = Mode.DISABLED;
	}
	
	private void setMotor(double percentage) {
		m_percent_out = percentage;
		m_motor_tall.set(ControlMode.PercentOutput, m_percent_out);
		SmartDashboard.putNumber("Tall Out", m_percent_out);
	}
	
	public void RunPeriodic() {
		if (m_mode == Mode.DISABLED) {
			setMotor(0.0);
			m_mode = Mode.MANUAL;
			return;
		}
		if (m_mode == Mode.MANUAL) {
			if (m_joystick.getRawButton(cButtonSetPos)) {
				m_motor_tall.setSelectedSensorPosition(0, 0, 0);
			}
			if (m_joystick.getRawButton(cButtonSeek)) {
				setMotor(0.0);
				m_mode = Mode.SEEK0;
				return;
			}
			if (m_joystick.getRawButton(cButtonManualUp)) { setMotor(0.25); }
			else if (m_joystick.getRawButton(cButtonManualDown)) { setMotor(-0.25); } 
			else { setMotor(0.0); }
			return;
		}
		if (m_mode == Mode.SEEK0) {
			// First determine if we should abort seeking...
			if (m_joystick.getRawButton(cButtonManualUp) || m_joystick.getRawButton(cButtonManualDown)) {
				// yes, return to manual mode.
				setMotor(0.0);
				m_mode = Mode.MANUAL;
				return;
			}
			double current_pos = m_motor_tall.getSelectedSensorPosition(0);
			double err = Math.abs(current_pos);
			if (err < 10.0) {
				// We are near enough to zero... turn motor off.
				setMotor(0.0);
				return;
			}
			double demand = err  / 6000.0;
			if (demand < 0.075) {
				demand = 0.075;
			}
			if (current_pos > 0.0) {
				// Drive positive till we get there.
				setMotor(demand);
			} else if (current_pos < 0.0) {
				// Drive negative till we get there.
				setMotor(-demand);
			} else {
				// We should never get here, since we already checked for this.
				// So, just to be safe, set the motor to zero.
				setMotor(0.0);
			}
			return;
		}
	}
}
