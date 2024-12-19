package adventOfCode2019;

import java.util.HashMap;
import java.util.Map;

public enum ParameterMode
{
	POSITION(0),
	IMMEDIATE(1),
	RELATIVE(2);
	
	public final int operation;
	
	private ParameterMode(int operation)
	{
		this.operation = operation;
	}

	private static final Map<Integer, ParameterMode> BY_OPERATION = new HashMap<>();

	static { for (ParameterMode pm : values())
		BY_OPERATION.put(pm.operation, pm);
	}
	
	public static ParameterMode valueOfOperation(int operation)
	{
		return BY_OPERATION.get(operation);
	}
}