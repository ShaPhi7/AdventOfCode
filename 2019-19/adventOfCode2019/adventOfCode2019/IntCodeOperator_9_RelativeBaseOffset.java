package adventOfCode2019;

public class IntCodeOperator_9_RelativeBaseOffset implements IntCodeOperator {

	@Override
	public OperationMode getOperationMode() {
		return OperationMode.valueOfOperation(9);
	}

	@Override
	public void operate(IntCode intCode, OpCode opCode) {
		
		long parameterValue = intCode.getValueForFirstParameterAccordingToParameterMode(opCode);
			
		Long parameterValueLong = Long.valueOf(parameterValue);
		intCode.offsetRelativeBase(parameterValueLong.intValue());
		
		intCode.logIfDebugIntCode("changing relativeBase by " + parameterValue 
			+ " as opCode is " + opCode.getOperationMode() + " for parameters " + opCode.getParameterModes());
		
		updateInstructionPointer(intCode);
	}

	@Override
	public void updateInstructionPointer(IntCode intCode) {
		intCode.increaseInstructionPointer(2);
		intCode.setUpdatedInstructionPointer(true);
	}

}
