// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.fasterxml.jackson.databind.util.RootNameLookup;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static frc.robot.Constants.GripperConstants.*;

import java.util.List;

import frc.robot.Limelight;

public class GripperSystem extends SubsystemBase implements Testable {

  //controls the speed of the spinning wheels
  //private final ColorSensorV3 colorSensor;
  // controls the speed of the spinning wheels
  private CANSparkMax rollerMotor;
  private Limelight limelight;
  private boolean isHolding;

  private double lastPosition;

  /** Creates a new GripperSystem. */
  public GripperSystem(Limelight limelight) {
    //colorSensor = new ColorSensorV3(GripperConstants.I2C_PORT);
    rollerMotor = new CANSparkMax(ROLLER_MOTOR, MotorType.kBrushless);
    this.limelight = limelight;
    rollerMotor.setSmartCurrentLimit(ROLLER_MOTOR_CURRENT_LIMIT_VALUE);
    rollerMotor.setInverted(true);

    isHolding = true;
  }

  public void spin(double speed) {
    rollerMotor.set(speed);
  }

  /**
   * Spins the gripper roller to intake
   * sets speed to 0 to stop
   **/
  public CommandBase coneIntake(AddressableLEDSubsystem aLedSubsystem){
    return runEnd(
      // run
      () -> {
        if (rollerMotor.getOutputCurrent() < DEFAULT_DRAW)
        {
          spin(ROLLER_SPEED);
        }
        else
        {
          spin(0);
        }
      },

      // end
      () -> {
        spin(0);
        isHolding = true;
      });
  }

  public CommandBase coneIntake() {
    return runEnd(
      // run
      () -> {
        if (rollerMotor.getOutputCurrent() < MAX_CUBE_DRAW)
        {
          spin(ROLLER_SPEED);
        }
        else
        {
          spin(0);
        }
      },

      // end
      () -> {
        spin(0);
        isHolding = true;
      });
  }

  public CommandBase cubeIntake(AddressableLEDSubsystem aLedSubsystem){
    return runEnd(
      // run
      () -> {
        if (rollerMotor.getOutputCurrent() < MAX_CUBE_DRAW)
        {
          spin(ROLLER_SPEED);
        }
        else
        {
          spin(0);
        }
      },

      // end
      () -> {
        spin(0);
        isHolding = true;
      });
  }

  public CommandBase hold() {
    return runEnd(
      //run
      () -> {
          if(isHolding){
            spin(0.15);
          }
          else
          {
            spin(0);
          }
        },
        // end
        () -> {
          spin(0);
        });
  }

  /**
   * spins the gripper roller at a negative speed to outtake
   * sets speed to 0 to stop
   **/
  public CommandBase outtake(AddressableLEDSubsystem aLedSubsystem) {
    return runEnd(
        // run
        () -> {
          spin(-ROLLER_SPEED);
        },
        // end
        () -> {
          spin(0);
          isHolding = false;
        });
  }

  public CommandBase coneOuttake(AddressableLEDSubsystem aLedSubsystem) {
    return runEnd(
      () -> {

        spin(-(CONE_SPEED));

      },

      () -> {
        
        spin(0);
        isHolding = false;

      });
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("GripperSystem");
    builder.addDoubleProperty("Current Draw Readings", () -> rollerMotor.getOutputCurrent(), null);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    lastPosition = rollerMotor.getEncoder().getPosition();
    System.out.println(lastPosition);
  }

  @Override
  public List<Connection> hardwareConnections() {
    return List.of(
      Connection.fromSparkMax(rollerMotor)
    );
  }

  @Override
  public CommandBase testRoutine() {
    return Commands.sequence(
      // run intake
      cubeIntake(null).withTimeout(1.5)
    );
  }
}
