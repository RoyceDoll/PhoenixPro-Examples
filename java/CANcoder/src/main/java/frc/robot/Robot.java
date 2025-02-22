// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private final double PRINT_PERIOD = 0.5; // Update every 500 ms

  private final CANcoder cancoder = new CANcoder(1, "rio");
  private double currentTime = Timer.getFPGATimestamp();

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    /* Configure CANcoder */
    var toApply = new CANcoderConfiguration();

    /* User can change the configs if they want, or leave it empty for factory-default */

    cancoder.getConfigurator().apply(toApply);

    /* Speed up signals to an appropriate rate */
    cancoder.getPosition().setUpdateFrequency(100);
    cancoder.getVelocity().setUpdateFrequency(100);
  }

  @Override
  public void robotPeriodic() {
    if (Timer.getFPGATimestamp() - currentTime > PRINT_PERIOD) {
      currentTime += PRINT_PERIOD;

      /**
       * getPosition automatically calls refresh(), no need to manually refresh.
       * 
       * StatusSignalValues also have the toString method implemented, to provide
       * a useful print of the signal.
       */
      var pos = cancoder.getPosition();
      System.out.println("Position is " + pos.toString() + " with " + pos.getTimestamp().getLatency() + " seconds of latency");

      /**
       * Get the velocity StatusSignalValue
       */
      var vel = cancoder.getVelocity();
      /* This time wait for the signal to reduce latency */
      vel.waitForUpdate(PRINT_PERIOD); // Wait up to our period
      /**
       * This uses the explicit getValue and getUnits functions to print, even though it's not
       * necessary for the ostream print
       */
      System.out.println("Velocity is " +
                         vel.getValue() + " " +
                         vel.getUnits() + " with " +
                         vel.getTimestamp().getLatency() + " seconds of latency");
      /**
       * Notice when running this example that the second print's latency is always shorter than the first print's latency.
       * This is because we explicitly wait for the signal using the waitForUpdate() method instead of using the refresh()
       * method, which only gets the last cached value (similar to how Phoenix v5 works).
       * This can be used to make sure we synchronously update our control loop from the CAN bus, reducing any latency or jitter in
       * CAN bus measurements.
       * When the device is on a CANivore, the reported latency is very close to the true latency of the sensor, as the CANivore
       * timestamps when it receives the frame. This can be further used for latency compensation.
       */
      System.out.println();
    }
  }

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    /**
     * When we teleop init, set the position of the Pigeon2 and wait for the setter to take affect.
     */
    cancoder.setPosition(0.4, 0.1); // Set our position to .4 rotations and wait up to 100 ms for the setter to take affect
    cancoder.getPosition().waitForUpdate(0.1); // And wait up to 100 ms for the position to take affect
    System.out.println("Set the position to 0.4 rotations, we are currently at " + cancoder.getPosition()); // Use java's implicit toString operator
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {}
}
