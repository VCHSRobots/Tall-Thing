// --------------------------------------------------------------------
// Robot.java -- Main robot run code
//
// Created 2018-10-19  DLB
// --------------------------------------------------------------------

package org.usfirst.frc.team4415.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import ecommon.RobotMap;

public class Robot extends IterativeRobot {
	private Joystick m_joystick = new Joystick(RobotMap.MainJoystick);
	private TallThing m_tallthing = new TallThing();
	//private RpmMotor m_rpmtester = new RpmMotor();
	
	@Override
	// Called once when the robot is turned on.
	public void robotInit() {
		m_tallthing.Initialize(m_joystick);
		//m_rpmtester.Initialize(m_joystick);
	}
	
	@Override 
	// Called in a loop for all modes.  Called after the
	// more specific mode periodic function is called.
	public void robotPeriodic() {
		// Send back status to SmartDashboard
		m_tallthing.Report();
		//m_rpmtester.Report();
	}
	
	@Override
	// Called at power on and when the robot changes 
	// from one of the enabled modes (teleop, auto, or test)
	public void disabledInit() {
		m_tallthing.Disable();
		//m_rpmtester.Disable();
	}
	
	@Override 
	// Called repeatedly when the robot is disabled.
	public void disabledPeriodic() {
	}
	
	@Override
	// Called once when the robot transitions into teleop mode.
	public void teleopInit() {
		//m_rpmtester.StartPeriodic();
	}
	
	@Override
	// Called repeatedly when the robot is in teleop mode.
	public void teleopPeriodic() {
		m_tallthing.RunPeriodic();
		//m_rpmtester.RunPeriodic();
	}

	@Override
	// Called once when the robot transitions into autonomous mode.
	public void autonomousInit() {
	}

	@Override
	// Called repeatedly when the robot is in autonomous mode.
	public void autonomousPeriodic() {
	}
	
	@Override
	// Called once when the robot transitions into test mode.
	public void testInit() {
	}	 
	
	@Override
	// Called repeatedly when the robot is in test mode..
	public void testPeriodic() {
	}
}