package grondag.mcmd.parser.block;

import grondag.mcmd.internal.BlockStartImpl;

/**
 * Result object for starting parsing of a block, see static methods for constructors.
 */
public abstract class BlockStart {

    protected BlockStart() {
    }

    public static BlockStart none() {
        return null;
    }

    public static BlockStart of(BlockParser... blockParsers) {
        return new BlockStartImpl(blockParsers);
    }

    public abstract BlockStart atIndex(int newIndex);

    public abstract BlockStart atColumn(int newColumn);

    public abstract BlockStart replaceActiveBlockParser();

}
