package adventOfCode2019;

import java.util.Vector;

public class IntCodeOperator_3_Input implements IntCodeOperator {

	@Override
	public OperationMode getOperationMode() {
		return OperationMode.valueOfOperation(3);
	}

	@Override
	public void operate(IntCode intCode, OpCode opCode)
	{
		int indexToOverwriteWithUserInput = intCode.getIndexForFirstParameterAccordingToParameterMode(opCode);

		Long nextUserInput = intCode.getNextUserInputOrPauseIfNone();
		
		if (nextUserInput != null)
		{
			Vector<Long> intCodes = intCode.getIntCodes();
			intCodes.set(indexToOverwriteWithUserInput, nextUserInput);

			intCode.logIfDebugIntCode("taken input " + nextUserInput + " and set at " + indexToOverwriteWithUserInput);
			
			updateInstructionPointer(intCode);
		}
	}

	@Override
	public void updateInstructionPointer(IntCode intCode) {

		intCode.increaseInstructionPointer(2);
		intCode.setUpdatedInstructionPointer(true);
	}

}
