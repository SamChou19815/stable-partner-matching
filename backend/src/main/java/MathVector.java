import java.util.Collection;
import java.util.List;

public class MathVector {
    int size;
    double[] arr;

    MathVector(int size) {
        this.size = size;
        arr = new double[size];
    }

    void fillFromList(List<Double> input) {
        assert (input.size() == size);
        for (int i = 0; i < size; i++) {
            arr[i] = input.get(i);
        }
    }

    void fillFromArr(double[] input) {
        assert (input.length == size);
        for (int i = 0; i < size; i++) {
            arr[i] = input[i];
        }
    }

    MathVector duplicate() {
        MathVector retval = new MathVector(size);
        retval.copyVector(this);
        return retval;
    }

    void copyVector(MathVector v) {
        size = v.getSize();
        arr = new double[size];
        for (int i = 0; i < size; i++) {
            arr[i] = v.get(i);
        }
    }

    void setAll(double v) {
        for (int i = 0; i < size; i++) {
            arr[i] = v;
        }
    }

    void addVector(MathVector v) {
        assert (v.getSize() == size);
        for (int i = 0; i < size; i++) {
            arr[i] += v.get(i);
        }
    }
    void scalarProduct(double v) {
        for (int i = 0; i < size; i++) {
            arr[i] *= v;
        }
    }

    void vectorProduct(MathVector v) {
        assert (v.getSize() == size);
        for (int i = 0; i < size; i++) {
            arr[i] *= v.get(i);
        }
    }

    double dotProduct(MathVector v) {
        assert (v.getSize() == size);
        double sum = 0.0;
        for (int i = 0; i < size; i++) {
            sum += v.get(i) * arr[i];
        }
        return sum;
    }

    void abs() {
        for (int i = 0; i < size; i++) {
            arr[i] = Math.abs(arr[i]);
        }
    }

    void exp() {
        for (int i = 0; i < size; i++) {
            arr[i] = Math.exp(arr[i]);
        }
    }

    public double get(int i) {
        return arr[i];
    }

    public int getSize() {
        return size;
    }
}
