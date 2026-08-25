package filepacker;

/**
 * ProgressListener
 * ----------------
 * Simple callback used by Packer and Unpacker to report progress
 * messages, so both the console UI and the AWT GUI can display
 * live status without the core logic depending on either one.
 */
public interface ProgressListener
{
    void onMessage(String message);
}
