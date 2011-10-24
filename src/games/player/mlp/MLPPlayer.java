package games.player.mlp;

import java.util.Scanner;
import java.util.StringTokenizer;

import ec.EvolutionState;
import ec.util.Parameter;
import games.Board;
import games.player.Player;

public class MLPPlayer implements Player {

	private Layer[] layers;

	private int numLayers;

	private int numInputs;

	public void setup(EvolutionState state, Parameter base) {
		// TODO Auto-generated method stub

	}

	public double evaluate(Board board) {
		double[] input = new double[board.getSize() * board.getSize()];
		for (int i = 0; i < input.length; i++) {
			input[i] = board.getValueAt(i);
		}
		
		double[] output = input;
		for (Layer layer : layers) {
			output = layer.propagate(output);
		}
		
		return output[0];
	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	public static MLPPlayer readFromString(String s) {
		MLPPlayer mlp = new MLPPlayer();
		Scanner scanner = new Scanner(s);
		StringTokenizer layersTokenizer = new StringTokenizer(scanner.nextLine());

		mlp.numLayers = layersTokenizer.countTokens() - 1;
		mlp.numInputs = Integer.parseInt(layersTokenizer.nextToken());
		int[] numNeuronsInLayers = new int[mlp.numLayers];
		for (int layer = 0; layer < mlp.numLayers; layer++) {
			numNeuronsInLayers[layer] = Integer.parseInt(layersTokenizer.nextToken());
		}

		mlp.layers = new Layer[mlp.numLayers];
		int numNeuronWeights = mlp.numInputs;
		for (int layer = 0; layer < mlp.numLayers; layer++) {
			Neuron[] neurons = new Neuron[numNeuronsInLayers[layer]];
			for (int neuron = 0; neuron < numNeuronsInLayers[layer]; neuron++) {
				neurons[neuron] = Neuron.readFromString(scanner.nextLine(), numNeuronWeights);
			}
			mlp.layers[layer] = new Layer(neurons);
			numNeuronWeights = numNeuronsInLayers[layer];
		}

		return mlp;
	}
}
