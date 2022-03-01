import java.util.Arrays;

class Position {
    int[][] data;
    int[] height;

    /**
     * Creates a new blank Position object (7x6).
     * 
     * Contains 2 attributes. 
     * <ul>
     * <li><strong>data:</strong> A two dimensional array containing the data for the array.
     * 0 is blank, 1 is first player, 2 is second player. <em>Note that the first column is column 0</em></li>
     * <li><strong>height:</strong> An array containing the current heights of all the columns in data.</li>
     * </ul>
     * <em>Note:</em> The data in a column at index height will be 0, and the next token placed will be placed 
     * at index height because array indexes start at 0.
     */
    public Position() {
        data = new int[][] {
            {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, 
            {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, 
            {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}
        };
        height = new int[] {0, 0, 0, 0, 0, 0, 0};
    }

    /**
     * Adds value to the top of the specified column.
     * 
     * @param column  The column to add to.
     * @param value  The value to add. Should be a 1 or a 2.
     * @throws IndexOutOfBoundsException  If column is already full.
     */
    public void push(int column, int value) {
        if (height[column] < 6) {
            data[column][height[column]] = value;
            height[column]++;
        } else {
            throw new IndexOutOfBoundsException("A token was placed when the column was already full!");
        } 
    }

    /**
     * Removes and returns the top value of the column (stack).
     * @return  The value that was removed from the top of the column (stack).
     */
    public int pop(int column) {
        int val = data[column][height[column] - 1];
        data[column][height[column] - 1] = 0;
        height[column]--;
        return val;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Position)) {
            return false;
        }

        Position other = (Position) o;
        return Arrays.deepEquals(data, other.data);
    }
    //TODO: Override hashCode
}
