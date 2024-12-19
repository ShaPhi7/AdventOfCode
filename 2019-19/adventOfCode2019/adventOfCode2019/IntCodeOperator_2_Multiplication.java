package adventOfCode2019;

public class IntCodeOperator_2_Multiplication implements IntCodeOperator {

	@Override
	public OperationMode getOperationMode() {
		OperationMode.valueOfOperation(2);
		return null;
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
