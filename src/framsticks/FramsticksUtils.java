package framsticks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;

import ec.EvolutionState;
import ec.util.Parameter;

public class FramsticksUtils {

	private static final String NEW_CMD = "%s \"getsimplest 1\" -q";
	private static final String EVAL_CMD = "%s \"ex %s\" \"eval %s %s\" -q";
	private static final String MUTATE_CMD = "%s rnd \"mutate %s\" -q";
	private static final String XOVER_CMD = "%s rnd \"crossover %s %s\" -q";

	private static final String GENOTYPE_DESC = "org:\ngenotype:~\n%s~\n";
	private static final String TEMPORARY_FILE_NAME = "temp.gen";

	private static final String P_DIRECTORY_PATH = "directory-path";
	private static final String P_SCRIPTS_OUTPUT = "scripts-output";
	private static final String P_SETTINGS = "settings-file";
	private static final String P_WORKING_DIRECTORY = "working-directory";
	private static final String P_EXPERIMENT_DEFINITION = "expdef";
	private static final String P_EXECUTABLE_COMMAND = "executable-cmd";

	private String directoryPath;
	private String scriptsOutputPath;
	private String settingsFile;
	private String workingDirectory;
	private String experimentDefinition;
	private String executableCommand;

	private static FramsticksUtils instance;

	public synchronized static FramsticksUtils getInstance(final EvolutionState state) {
		if (instance == null) {
			instance = new FramsticksUtils();
			instance.setup(state);
		}
		return instance;
	}

	private void setup(final EvolutionState state) {
		Parameter def = FramsticksDefaults.base();
		directoryPath = state.parameters.getString(null, def.push(P_DIRECTORY_PATH));
		if (directoryPath == null) {
			state.output.fatal("No Framsticks directory specified", def.push(P_DIRECTORY_PATH));
		}
		scriptsOutputPath = state.parameters.getString(null, def.push(P_SCRIPTS_OUTPUT));
		if (scriptsOutputPath == null) {
			state.output.fatal("No scripts output file specified", def.push(P_SCRIPTS_OUTPUT));
		}
		settingsFile = state.parameters.getString(null, def.push(P_SETTINGS));
		if (settingsFile == null) {
			state.output.fatal("No settings file specified", def.push(P_SETTINGS));
		}
		workingDirectory = state.parameters.getString(null, def.push(P_WORKING_DIRECTORY));
		if (workingDirectory == null) {
			state.output.fatal("No working directory specified", def.push(P_WORKING_DIRECTORY));
		}
		experimentDefinition = state.parameters.getString(null, def.push(P_EXPERIMENT_DEFINITION));
		if (experimentDefinition == null) {
			state.output.fatal("No experiment definition specified", def
					.push(P_EXPERIMENT_DEFINITION));
		}
		executableCommand = state.parameters.getString(null, def.push(P_EXECUTABLE_COMMAND));
		if (executableCommand == null) {
			state.output.fatal("No executable command specified", def.push(P_EXECUTABLE_COMMAND));
		}
	}

	private String executeCommand(String command) {
		String line;
		String result = "";
		try {
			File f = new File(directoryPath);
			Process p = Runtime.getRuntime().exec(directoryPath + command, null, f);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				result += (line + '\n');
			}
			input.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private void saveToFile(String filePath, String contents) {
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(filePath));
			output.write(contents);
			output.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String readFromFile(String filePath) {
		String line;
		String result = "";
		try {
			BufferedReader input = new BufferedReader(new FileReader(filePath));
			while ((line = input.readLine()) != null) {
				result += (line + '\n');
			}
			input.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	public float evaluateGenotype(String genotype, String fileName) {
		String fileContents = String.format(GENOTYPE_DESC, genotype);
		String filePath = workingDirectory + fileName;

		saveToFile(filePath, fileContents);
		executeCommand(String.format(EVAL_CMD, executableCommand, experimentDefinition,
				settingsFile, filePath));
		String evaluation = readFromFile(scriptsOutputPath);

		return Float.parseFloat(evaluation.split("\t")[1]);
	}

	public int coevolutionaryEvaluate(String candidate, String test,
			String fileName) {
		int candidateResult = (int)evaluateGenotype(candidate, fileName);
		int testResult = (int)evaluateGenotype(test, fileName);

		return candidateResult - testResult;
	}

	public String mutateGenotype(String genotype) {
		String filePath = workingDirectory + TEMPORARY_FILE_NAME;
		saveToFile(filePath, genotype);
		return executeCommand(String.format(MUTATE_CMD, executableCommand, filePath));
	}

	public String crossoverGenotypes(String genotype1, String genotype2) {
		String filePath1 = workingDirectory + TEMPORARY_FILE_NAME;
		String filePath2 = workingDirectory + "_" + TEMPORARY_FILE_NAME;
		saveToFile(filePath1, genotype1);
		saveToFile(filePath2, genotype2);
		return executeCommand(String.format(XOVER_CMD, executableCommand, filePath1, filePath2));
	}

	public String getNewGenotype() {
		return executeCommand(String.format(NEW_CMD, executableCommand));
	}

	/*
	 * Sample usage :
	 */
	/*
	 * public static void main(String[] args) { FramsticksUtils utils =
	 * FramsticksUtils.getInstance(); System.out.println(utils.evaluateGenotype("X", "halo1.gen"));
	 * System.out.println(utils.mutateGenotype("X"));
	 * System.out.println(utils.crossoverGenotypes("AX", "MX"));
	 * System.out.println(utils.getNewGenotype()); }
	 */
}
