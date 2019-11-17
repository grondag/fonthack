package grondag.mcmd.internal.inline;

import grondag.mcmd.node.Node;
import grondag.mcmd.node.Strikethrough;
import grondag.mcmd.node.Text;
import grondag.mcmd.parser.delimiter.DelimiterProcessor;
import grondag.mcmd.parser.delimiter.DelimiterRun;

public class StrikethroughDelimiterProcessor implements DelimiterProcessor {

    @Override
    public char getOpeningCharacter() {
        return '~';
    }

    @Override
    public char getClosingCharacter() {
        return '~';
    }

    @Override
    public int getMinLength() {
        return 2;
    }

    @Override
    public int getDelimiterUse(DelimiterRun opener, DelimiterRun closer) {
        if (opener.length() >= 2 && closer.length() >= 2) {
            // Use exactly two delimiters even if we have more, and don't care about internal openers/closers.
            return 2;
        } else {
            return 0;
        }
    }

    @Override
    public void process(Text opener, Text closer, int delimiterCount) {
        // Wrap nodes between delimiters in strikethrough.
        final Node strikethrough = new Strikethrough();

        Node tmp = opener.getNext();
        while (tmp != null && tmp != closer) {
            final Node next = tmp.getNext();
            strikethrough.appendChild(tmp);
            tmp = next;
        }

        opener.insertAfter(strikethrough);
    }
}