package games.player.mlp;

public class Layer {

	private Neuron[] neurons;
	private double[] output;

	public Layer(Neuron[] neurons) {
		this.neurons = neurons;
		this.output = new double[neurons.length];
	}

	public double[] propagate(double[] input) {
		for (int neuron = 0; neuron < neurons.length; neuron++) {
			output[neuron] = neurons[neuron].propagate(input);
		}
		return output;
	}
}
