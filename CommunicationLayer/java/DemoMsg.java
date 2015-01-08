/**
 * This class is automatically generated by mig. DO NOT EDIT THIS FILE.
 * This class implements a Java interface to the 'DemoMsg'
 * message type.
 */

public class DemoMsg extends net.tinyos.message.Message {

    /** The default size of this message type in bytes. */
    public static final int DEFAULT_MESSAGE_SIZE = 5;

    /** The Active Message type associated with this message. */
    public static final int AM_TYPE = 32;

    /** Create a new DemoMsg of size 5. */
    public DemoMsg() {
        super(DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /** Create a new DemoMsg of the given data_length. */
    public DemoMsg(int data_length) {
        super(data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new DemoMsg with the given data_length
     * and base offset.
     */
    public DemoMsg(int data_length, int base_offset) {
        super(data_length, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new DemoMsg using the given byte array
     * as backing store.
     */
    public DemoMsg(byte[] data) {
        super(data);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new DemoMsg using the given byte array
     * as backing store, with the given base offset.
     */
    public DemoMsg(byte[] data, int base_offset) {
        super(data, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new DemoMsg using the given byte array
     * as backing store, with the given base offset and data length.
     */
    public DemoMsg(byte[] data, int base_offset, int data_length) {
        super(data, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new DemoMsg embedded in the given message
     * at the given base offset.
     */
    public DemoMsg(net.tinyos.message.Message msg, int base_offset) {
        super(msg, base_offset, DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new DemoMsg embedded in the given message
     * at the given base offset and length.
     */
    public DemoMsg(net.tinyos.message.Message msg, int base_offset, int data_length) {
        super(msg, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
    /* Return a String representation of this message. Includes the
     * message type name and the non-indexed field values.
     */
    public String toString() {
      String s = "Message <DemoMsg> \n";
      try {
        s += "  [msg_type=0x"+Long.toHexString(get_msg_type())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [sensorsBitmask=0x"+Long.toHexString(get_sensorsBitmask())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [period=0x"+Long.toHexString(get_period())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      return s;
    }

    // Message-type-specific access methods appear below.

    /////////////////////////////////////////////////////////
    // Accessor methods for field: msg_type
    //   Field type: short, unsigned
    //   Offset (bits): 0
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'msg_type' is signed (false).
     */
    public static boolean isSigned_msg_type() {
        return false;
    }

    /**
     * Return whether the field 'msg_type' is an array (false).
     */
    public static boolean isArray_msg_type() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'msg_type'
     */
    public static int offset_msg_type() {
        return (0 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'msg_type'
     */
    public static int offsetBits_msg_type() {
        return 0;
    }

    /**
     * Return the value (as a short) of the field 'msg_type'
     */
    public short get_msg_type() {
        return (short)getUIntBEElement(offsetBits_msg_type(), 8);
    }

    /**
     * Set the value of the field 'msg_type'
     */
    public void set_msg_type(short value) {
        setUIntBEElement(offsetBits_msg_type(), 8, value);
    }

    /**
     * Return the size, in bytes, of the field 'msg_type'
     */
    public static int size_msg_type() {
        return (8 / 8);
    }

    /**
     * Return the size, in bits, of the field 'msg_type'
     */
    public static int sizeBits_msg_type() {
        return 8;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: sensorsBitmask
    //   Field type: int, unsigned
    //   Offset (bits): 8
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'sensorsBitmask' is signed (false).
     */
    public static boolean isSigned_sensorsBitmask() {
        return false;
    }

    /**
     * Return whether the field 'sensorsBitmask' is an array (false).
     */
    public static boolean isArray_sensorsBitmask() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'sensorsBitmask'
     */
    public static int offset_sensorsBitmask() {
        return (8 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'sensorsBitmask'
     */
    public static int offsetBits_sensorsBitmask() {
        return 8;
    }

    /**
     * Return the value (as a int) of the field 'sensorsBitmask'
     */
    public int get_sensorsBitmask() {
        return (int)getUIntBEElement(offsetBits_sensorsBitmask(), 16);
    }

    /**
     * Set the value of the field 'sensorsBitmask'
     */
    public void set_sensorsBitmask(int value) {
        setUIntBEElement(offsetBits_sensorsBitmask(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'sensorsBitmask'
     */
    public static int size_sensorsBitmask() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'sensorsBitmask'
     */
    public static int sizeBits_sensorsBitmask() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: period
    //   Field type: int, unsigned
    //   Offset (bits): 24
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'period' is signed (false).
     */
    public static boolean isSigned_period() {
        return false;
    }

    /**
     * Return whether the field 'period' is an array (false).
     */
    public static boolean isArray_period() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'period'
     */
    public static int offset_period() {
        return (24 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'period'
     */
    public static int offsetBits_period() {
        return 24;
    }

    /**
     * Return the value (as a int) of the field 'period'
     */
    public int get_period() {
        return (int)getUIntBEElement(offsetBits_period(), 16);
    }

    /**
     * Set the value of the field 'period'
     */
    public void set_period(int value) {
        setUIntBEElement(offsetBits_period(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'period'
     */
    public static int size_period() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'period'
     */
    public static int sizeBits_period() {
        return 16;
    }

}