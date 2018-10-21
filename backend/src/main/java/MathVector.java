import org.jetbrains.annotations.NotNull;

public final class MathVector {
    
    /**
     * The dimension of the vector.
     */
    private int size;
    /**
     * The underlying storage.
     */
    private double[] arr;
    
    /**
     * Initialize by the final size.
     *
     * @param size the final size.
     */
    MathVector(int size) {
        this.size = size;
        arr = new double[size];
    }
    
    /**
     * Update the vector with the contents in the array.
     *
     * @param input the array as the source of the update.
     */
    void updateFromArray(double[] input) {
        assert (input.length == size);
        System.arraycopy(input, 0, arr, 0, size);
    }
    
    /**
     * Return a deep copy of itself.
     *
     * @return a deep copy of itself.
     */
    @NotNull
    MathVector copy() {
        MathVector ans = new MathVector(size);
        ans.copyVector(this);
        return ans;
    }
    
    private void copyVector(MathVector v) {
        size = v.size;
        arr = new double[size];
        System.arraycopy(v.arr, 0, arr, 0, size);
    }
    
    void setAll(double v) {
        for (int i = 0; i < size; i++) {
            arr[i] = v;
        }
    }
    
    void addVector(MathVector v) {
        assert (v.size == size);
        for (int i = 0; i < size; i++) {
            arr[i] += v.arr[i];
        }
    }
    
    /**
     * Multiply v to every dimension.
     *
     * @param v the common multiplier.
     */
    void scalarProduct(double v) {
        for (int i = 0; i < size; i++) {
            arr[i] *= v;
        }
    }
    
    void vectorProduct(MathVector v) {
        assert (v.size == size);
        for (int i = 0; i < size; i++) {
            arr[i] *= v.arr[i];
        }
    }
    
    /**
     * Compute the dot product with another vector.
     * The function is pure.
     *
     * @param v another vector.
     * @return this .* v
     */
    double dotProduct(MathVector v) {
        assert (v.size == size);
        double sum = 0.0;
        for (int i = 0; i < size; i++) {
            sum += v.arr[i] * arr[i];
        }
        return sum;
    }
    
    /**
     * Apply abs to all of its elements.
     */
    void abs() {
        for (int i = 0; i < size; i++) {
            arr[i] = Math.abs(arr[i]);
        }
    }
    
    /**
     * Apply exp to all of its elements.
     */
    void exp() {
        for (int i = 0; i < size; i++) {
            arr[i] = Math.exp(arr[i]);
        }
    }
    
    /**
     * Return the value at dimension i.
     *
     * @return the value at dimension i.
     */
    public double get(int i) {
        return arr[i];
    }
    
}
