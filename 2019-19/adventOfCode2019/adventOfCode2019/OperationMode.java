package adventOfCode2019;

import java.util.HashMap;
import java.util.Map;

public enum OperationMode {

	ADDITION(1),
	MULTIPLICATION(2),
	INPUT(3),
	OUTPUT(4),
	JUMP_IF_NONZERO(5),
	JUMP_IF_ZERO(6),
	LESS_THAN(7),
	EQUAL_TO(8),
	RELATIVE_BASE_OFFSET(9),
	TERMINATION(99);

	public final long operation;

	private OperationMode(int operation)
	{
		this.operation = operation;
	}

	private static final Map<Long, OperationMode> BY_OPERATION = new HashMap<>();

	static { for (OperationMode o : values())
		BY_OPERATION.put(o.operation, o);
	}
	
	public static OperationMode valueOfOperation(long operationModeOperation)
	{
		return BY_OPERATION.get(operationModeOperation);
	}
	
	public static OperationMode valueOfOperation(int operationModeOperation)
	{
		return BY_OPERATION.get((long)operationModeOperation);
	}
	
}
