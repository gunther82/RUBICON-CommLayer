package cnr.rubicon.migFiles;
/**
 * This class is automatically generated by mig. DO NOT EDIT THIS FILE.
 * This class implements a Java interface to the 'LearningModuleDownMsg'
 * message type.
 */

public class LearningModuleDownMsg extends net.tinyos.message.Message {

    /** The default size of this message type in bytes. */
    public static final int DEFAULT_MESSAGE_SIZE = 96;

    /** The Active Message type associated with this message. */
    public static final int AM_TYPE = 24;

    /** Create a new LearningModuleDownMsg of size 96. */
    public LearningModuleDownMsg() {
        super(DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /** Create a new LearningModuleDownMsg of the given data_length. */
    public LearningModuleDownMsg(int data_length) {
        super(data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new LearningModuleDownMsg with the given data_length
     * and base offset.
     */
    public LearningModuleDownMsg(int data_length, int base_offset) {
        super(data_length, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new LearningModuleDownMsg using the given byte array
     * as backing store.
     */
    public LearningModuleDownMsg(byte[] data) {
        super(data);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new LearningModuleDownMsg using the given byte array
     * as backing store, with the given base offset.
     */
    public LearningModuleDownMsg(byte[] data, int base_offset) {
        super(data, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new LearningModuleDownMsg using the given byte array
     * as backing store, with the given base offset and data length.
     */
    public LearningModuleDownMsg(byte[] data, int base_offset, int data_length) {
        super(data, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new LearningModuleDownMsg embedded in the given message
     * at the given base offset.
     */
    public LearningModuleDownMsg(net.tinyos.message.Message msg, int base_offset) {
        super(msg, base_offset, DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new LearningModuleDownMsg embedded in the given message
     * at the given base offset and length.
     */
    public LearningModuleDownMsg(net.tinyos.message.Message msg, int base_offset, int data_length) {
        super(msg, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
    /* Return a String representation of this message. Includes the
     * message type name and the non-indexed field values.
     */
    public String toString() {
      String s = "Message <LearningModuleDownMsg> \n";
      try {
        s += "  [type=0x"+Long.toHexString(get_type())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [netId=0x"+Long.toHexString(get_netId())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [matrix_size=0x"+Long.toHexString(get_matrix_size())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [weights=";
        for (int i = 0; i < 23; i++) {
          s += Float.toString(getElement_weights(i))+" ";
        }
        s += "]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      return s;
    }

    // Message-type-specific access methods appear below.

    /////////////////////////////////////////////////////////
    // Accessor methods for field: type
    //   Field type: short
    //   Offset (bits): 0
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'type' is signed (true).
     */
    public static boolean isSigned_type() {
        return true;
    }

    /**
     * Return whether the field 'type' is an array (false).
     */
    public static boolean isArray_type() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'type'
     */
    public static int offset_type() {
        return (0 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'type'
     */
    public static int offsetBits_type() {
        return 0;
    }

    /**
     * Return the value (as a short) of the field 'type'
     */
    public short get_type() {
        return (short)getUIntBEElement(offsetBits_type(), 8);
    }

    /**
     * Set the value of the field 'type'
     */
    public void set_type(short value) {
        setUIntBEElement(offsetBits_type(), 8, value);
    }

    /**
     * Return the size, in bytes, of the field 'type'
     */
    public static int size_type() {
        return (8 / 8);
    }

    /**
     * Return the size, in bits, of the field 'type'
     */
    public static int sizeBits_type() {
        return 8;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: netId
    //   Field type: int
    //   Offset (bits): 8
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'netId' is signed (true).
     */
    public static boolean isSigned_netId() {
        return true;
    }

    /**
     * Return whether the field 'netId' is an array (false).
     */
    public static boolean isArray_netId() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'netId'
     */
    public static int offset_netId() {
        return (8 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'netId'
     */
    public static int offsetBits_netId() {
        return 8;
    }

    /**
     * Return the value (as a int) of the field 'netId'
     */
    public int get_netId() {
        return (int)getUIntBEElement(offsetBits_netId(), 16);
    }

    /**
     * Set the value of the field 'netId'
     */
    public void set_netId(int value) {
        setUIntBEElement(offsetBits_netId(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'netId'
     */
    public static int size_netId() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'netId'
     */
    public static int sizeBits_netId() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: matrix_size
    //   Field type: short
    //   Offset (bits): 24
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'matrix_size' is signed (true).
     */
    public static boolean isSigned_matrix_size() {
        return true;
    }

    /**
     * Return whether the field 'matrix_size' is an array (false).
     */
    public static boolean isArray_matrix_size() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'matrix_size'
     */
    public static int offset_matrix_size() {
        return (24 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'matrix_size'
     */
    public static int offsetBits_matrix_size() {
        return 24;
    }

    /**
     * Return the value (as a short) of the field 'matrix_size'
     */
    public short get_matrix_size() {
        return (short)getUIntBEElement(offsetBits_matrix_size(), 8);
    }

    /**
     * Set the value of the field 'matrix_size'
     */
    public void set_matrix_size(short value) {
        setUIntBEElement(offsetBits_matrix_size(), 8, value);
    }

    /**
     * Return the size, in bytes, of the field 'matrix_size'
     */
    public static int size_matrix_size() {
        return (8 / 8);
    }

    /**
     * Return the size, in bits, of the field 'matrix_size'
     */
    public static int sizeBits_matrix_size() {
        return 8;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: weights
    //   Field type: float[]
    //   Offset (bits): 32
    //   Size of each element (bits): 32
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'weights' is signed (true).
     */
    public static boolean isSigned_weights() {
        return true;
    }

    /**
     * Return whether the field 'weights' is an array (true).
     */
    public static boolean isArray_weights() {
        return true;
    }

    /**
     * Return the offset (in bytes) of the field 'weights'
     */
    public static int offset_weights(int index1) {
        int offset = 32;
        if (index1 < 0 || index1 >= 23) throw new ArrayIndexOutOfBoundsException();
        offset += 0 + index1 * 32;
        return (offset / 8);
    }

    /**
     * Return the offset (in bits) of the field 'weights'
     */
    public static int offsetBits_weights(int index1) {
        int offset = 32;
        if (index1 < 0 || index1 >= 23) throw new ArrayIndexOutOfBoundsException();
        offset += 0 + index1 * 32;
        return offset;
    }

    /**
     * Return the entire array 'weights' as a float[]
     */
    public float[] get_weights() {
        float[] tmp = new float[23];
        for (int index0 = 0; index0 < numElements_weights(0); index0++) {
            tmp[index0] = getElement_weights(index0);
        }
        return tmp;
    }

    /**
     * Set the contents of the array 'weights' from the given float[]
     */
    public void set_weights(float[] value) {
        for (int index0 = 0; index0 < value.length; index0++) {
            setElement_weights(index0, value[index0]);
        }
    }

    /**
     * Return an element (as a float) of the array 'weights'
     */
    public float getElement_weights(int index1) {
        return (float)getFloatElement(offsetBits_weights(index1), 32);
    }

    /**
     * Set an element of the array 'weights'
     */
    public void setElement_weights(int index1, float value) {
        setFloatElement(offsetBits_weights(index1), 32, value);
    }

    /**
     * Return the total size, in bytes, of the array 'weights'
     */
    public static int totalSize_weights() {
        return (736 / 8);
    }

    /**
     * Return the total size, in bits, of the array 'weights'
     */
    public static int totalSizeBits_weights() {
        return 736;
    }

    /**
     * Return the size, in bytes, of each element of the array 'weights'
     */
    public static int elementSize_weights() {
        return (32 / 8);
    }

    /**
     * Return the size, in bits, of each element of the array 'weights'
     */
    public static int elementSizeBits_weights() {
        return 32;
    }

    /**
     * Return the number of dimensions in the array 'weights'
     */
    public static int numDimensions_weights() {
        return 1;
    }

    /**
     * Return the number of elements in the array 'weights'
     */
    public static int numElements_weights() {
        return 23;
    }

    /**
     * Return the number of elements in the array 'weights'
     * for the given dimension.
     */
    public static int numElements_weights(int dimension) {
      int array_dims[] = { 23,  };
        if (dimension < 0 || dimension >= 1) throw new ArrayIndexOutOfBoundsException();
        if (array_dims[dimension] == 0) throw new IllegalArgumentException("Array dimension "+dimension+" has unknown size");
        return array_dims[dimension];
    }

}
