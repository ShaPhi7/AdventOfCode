package adventOfCode2019;

public class IntCodeOperator_8_EqualTo implements IntCodeOperator {

	@Override
	public OperationMode getOperationMode() {
		return OperationMode.valueOfOperation(8);
	}

	@Override
	public void operate(IntCode intCode, OpCode opCode) {
		intCode.processOpCodesWithThreeParms(opCode);
		
		updateInstructionPointer(intCode);
	}

	@Override
	public void updateInstructionPointer(IntCode intCode) {
		intCode.increaseInstructionPointer(4);
		intCode.setUpdatedInstructionPointer(true);
	}

}
