/**
 * This class is automatically generated by mig. DO NOT EDIT THIS FILE.
 * This class implements a Java interface to the 'LearningNotificationMsg'
 * message type.
 */

public class LearningNotificationMsg extends net.tinyos.message.Message {

    /** The default size of this message type in bytes. */
    public static final int DEFAULT_MESSAGE_SIZE = 4;

    /** The Active Message type associated with this message. */
    public static final int AM_TYPE = 23;

    /** Create a new LearningNotificationMsg of size 4. */
    public LearningNotificationMsg() {
        super(DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /** Create a new LearningNotificationMsg of the given data_length. */
    public LearningNotificationMsg(int data_length) {
        super(data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new LearningNotificationMsg with the given data_length
     * and base offset.
     */
    public LearningNotificationMsg(int data_length, int base_offset) {
        super(data_length, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new LearningNotificationMsg using the given byte array
     * as backing store.
     */
    public LearningNotificationMsg(byte[] data) {
        super(data);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new LearningNotificationMsg using the given byte array
     * as backing store, with the given base offset.
     */
    public LearningNotificationMsg(byte[] data, int base_offset) {
        super(data, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new LearningNotificationMsg using the given byte array
     * as backing store, with the given base offset and data length.
     */
    public LearningNotificationMsg(byte[] data, int base_offset, int data_length) {
        super(data, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new LearningNotificationMsg embedded in the given message
     * at the given base offset.
     */
    public LearningNotificationMsg(net.tinyos.message.Message msg, int base_offset) {
        super(msg, base_offset, DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new LearningNotificationMsg embedded in the given message
     * at the given base offset and length.
     */
    public LearningNotificationMsg(net.tinyos.message.Message msg, int base_offset, int data_length) {
        super(msg, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
    /* Return a String representation of this message. Includes the
     * message type name and the non-indexed field values.
     */
    public String toString() {
      String s = "Message <LearningNotificationMsg> \n";
      try {
        s += "  [type=0x"+Long.toHexString(get_type())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [seq_num=0x"+Long.toHexString(get_seq_num())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [ack=0x"+Long.toHexString(get_ack())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      return s;
    }

    // Message-type-specific access methods appear below.

    /////////////////////////////////////////////////////////
    // Accessor methods for field: type
    //   Field type: short, unsigned
    //   Offset (bits): 0
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'type' is signed (false).
     */
    public static boolean isSigned_type() {
        return false;
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
    // Accessor methods for field: seq_num
    //   Field type: int, unsigned
    //   Offset (bits): 8
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'seq_num' is signed (false).
     */
    public static boolean isSigned_seq_num() {
        return false;
    }

    /**
     * Return whether the field 'seq_num' is an array (false).
     */
    public static boolean isArray_seq_num() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'seq_num'
     */
    public static int offset_seq_num() {
        return (8 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'seq_num'
     */
    public static int offsetBits_seq_num() {
        return 8;
    }

    /**
     * Return the value (as a int) of the field 'seq_num'
     */
    public int get_seq_num() {
        return (int)getUIntBEElement(offsetBits_seq_num(), 16);
    }

    /**
     * Set the value of the field 'seq_num'
     */
    public void set_seq_num(int value) {
        setUIntBEElement(offsetBits_seq_num(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'seq_num'
     */
    public static int size_seq_num() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'seq_num'
     */
    public static int sizeBits_seq_num() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: ack
    //   Field type: short, unsigned
    //   Offset (bits): 24
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'ack' is signed (false).
     */
    public static boolean isSigned_ack() {
        return false;
    }

    /**
     * Return whether the field 'ack' is an array (false).
     */
    public static boolean isArray_ack() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'ack'
     */
    public static int offset_ack() {
        return (24 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'ack'
     */
    public static int offsetBits_ack() {
        return 24;
    }

    /**
     * Return the value (as a short) of the field 'ack'
     */
    public short get_ack() {
        return (short)getUIntBEElement(offsetBits_ack(), 8);
    }

    /**
     * Set the value of the field 'ack'
     */
    public void set_ack(short value) {
        setUIntBEElement(offsetBits_ack(), 8, value);
    }

    /**
     * Return the size, in bytes, of the field 'ack'
     */
    public static int size_ack() {
        return (8 / 8);
    }

    /**
     * Return the size, in bits, of the field 'ack'
     */
    public static int sizeBits_ack() {
        return 8;
    }

}
