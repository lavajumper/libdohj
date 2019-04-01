package org.libdohj.params;

import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptOpCodes;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.utils.MonetaryFormat;
import org.libdohj.core.AltcoinSerializer;
import org.libdohj.core.AuxPoWNetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;


import static org.bitcoinj.core.Coin.COIN;

/**
 * Created by Lavajumper on 1/12/2019.
 */
public abstract class AbstractSexcoinParams extends NetworkParameters implements AuxPoWNetworkParameters {

    /**
     * Standard format for the SXC denomination.
     */
    public static final MonetaryFormat SXC;
    /**
     * Standard format for the mSXC denomination.
     */
    public static final MonetaryFormat MSXC;
    /**
     * Standard format for the uSXC denomination.
     */
    public static final MonetaryFormat USXC;

    public static final int AUXPOW_CHAIN_ID = 0x0069;           // 98
    public static final int SXC_TARGET_TIMESPAN = 8 * 60 * 60;  // 8 hours per difficulty cycle, on average.
    public static final int SXC_TARGET_TIMESPAN_F1 = 30 * 60;   // Fork1 - reduced to 30 minutes
    public static final int SXC_TARGET_TIMESPAN_KGW = 15 * 60;  // 15 Minute retarget

    public static final int SXC_TARGET_SPACING = 60;            // 1 minute per block.
    public static final int SXC_TARGET_SPACING_F1 = 30;         // 30 second blocks
    public static final int SXC_TARGET_SPACING_KGW = 60;        // 60 second blocks again

    public static final int SXC_INTERVAL = SXC_TARGET_TIMESPAN / SXC_TARGET_SPACING;
    public static final int SXC_INTERVAL_F1 = SXC_TARGET_TIMESPAN_F1 / SXC_TARGET_SPACING_F1;
    public static final int SXC_INTERVAL_KGW = SXC_TARGET_TIMESPAN_KGW / SXC_TARGET_SPACING_KGW;
    public static final int SXC_INTERVAL_KGW_FIX = 1; // retarget every block


    public static final int SXC_FORK1_HEIGHT = 155000;
    public static final int SXC_KGW_HEIGHT = 572000;
    public static final int SXC_KGW_FIX_HEIGHT = 643808;
    public static final int SXC_AGE_VERIF_HEIGHT = 2348569;
    public static final int SXC_AUXPOW_HEIGHT = 3080000;
    public static final int SXC_SEGWIT_HEIGHT = 3106030;

    /**
     * Currency code for base 1 Sexcoin.
     */
    public static final String CODE_SXC = "SXC";
    /**
     * Currency code for base 1/1,000 Sexcoin.
     */
    public static final String CODE_MSXC = "mSXC";
    /**
     * Currency code for base 1/100,000,000 Sexcoin.
     */
    public static final String CODE_USXC = "uSXC";

    private static final int BLOCK_MIN_VERSION_AUXPOW = 0x00620002;
    private static final int BLOCK_VERSION_FLAG_AUXPOW = 0x00000100;

    static {
        SXC = MonetaryFormat.BTC.noCode()
                .code(0, CODE_SXC)
                .code(3, CODE_MSXC)
                .code(7, CODE_USXC);
        MSXC = SXC.shift(3).minDecimals(2).optionalDecimals(2);
        USXC = SXC.shift(7).minDecimals(0).optionalDecimals(2);
    }

    /**
     * The string returned by getId() for the main, production network where people trade things.
     */
    public static final String ID_SXC_MAINNET = "org.sexcoin.production";
    /**
     * The string returned by getId() for the testnet.
     */
    public static final String ID_SXC_TESTNET = "org.sexcoin.test";
    public static final String ID_SXC_REGTEST = "org.sexcoin.regtest";

    protected final int diffChangeTarget;
    protected boolean skipDiffChecks;

    protected Logger log = LoggerFactory.getLogger(AbstractSexcoinParams.class);
    public static final int SXC_PROTOCOL_VERSION_AUXPOW = 70015;
    public static final int SXC_PROTOCOL_VERSION_CURRENT = 70015;

    private static final Coin BASE_SUBSIDY = COIN.multiply(50);
    private static final Coin STABLE_SUBSIDY = COIN.multiply(100);


    public AbstractSexcoinParams(final int setDiffChangeTarget) {
        super();
        genesisBlock = createGenesis(this);
        interval = SXC_INTERVAL;
        targetTimespan = SXC_TARGET_TIMESPAN;
        maxTarget = Utils.decodeCompactBits(0x1f01ffffL); //520224767L
        diffChangeTarget = setDiffChangeTarget;
        //subsidyDecreaseBlockCount=600000;

        packetMagic = 0xface6969;
        bip32HeaderPub = 0x0488B21E; //The 4 byte header that serializes in base58 to "xpub". (?)
        bip32HeaderPriv = 0x0488ADE4; //The 4 byte header that serializes in base58 to "xprv" (?)

        skipDiffChecks = false;


    }

    private static AltcoinBlock createGenesis(NetworkParameters params) {
        AltcoinBlock genesisBlock = new AltcoinBlock(params, Block.BLOCK_VERSION_GENESIS);
        Transaction t = new Transaction(params);
        try {
            byte[] bytes = Utils.HEX.decode
                    ("04ffff001d01042144697361737465722066726f6d2074686520736b7920696e204f6b6c61686f6d61");
            t.addInput(new TransactionInput(params, t, bytes));
            ByteArrayOutputStream scriptPubKeyBytes = new ByteArrayOutputStream();
            Script.writeBytes(scriptPubKeyBytes, Utils.HEX.decode
                    ("04a5814813115273a109cff99907ba4a05d951873dae7acb6c973d0c9e7c88911a3dbc9aa600deac241b91707e7b4ffb30ad91c8e56e695a1ddf318592988afe0a"));
            scriptPubKeyBytes.write(ScriptOpCodes.OP_CHECKSIG);
            t.addOutput(new TransactionOutput(params, t, COIN.multiply(50), scriptPubKeyBytes.toByteArray()));
        } catch (Exception e) {
            // Cannot happen.
            throw new RuntimeException(e);
        }
        genesisBlock.addTransaction(t);
        return genesisBlock;
    }

    public void setSkipDiffChecks(boolean b){
        this.skipDiffChecks=b;
    }

    @Override
    public Coin getBlockSubsidy(final int height) {
        if ( height < 1){
            return BASE_SUBSIDY;
        }else if ( height < 3 ){
            return COIN.multiply(1000000);
        } else if(height < 5001) {
            return COIN.multiply(200);
        } else {
            MersenneTwister mt = new MersenneTwister(new int[]{height, 100000});
            int rand = mt.nextInt();

            if(rand > 99990){
               STABLE_SUBSIDY.multiply(50);
            }else if (rand < 2001){
               STABLE_SUBSIDY.multiply(5);
            }
            return STABLE_SUBSIDY.shiftRight( height / getSubsidyDecreaseBlockCount());
        }

    }

    /**
     * How many blocks pass between difficulty adjustment periods. After new diff algo.
     */
    public int getInterval(int height){
        if( height < SXC_FORK1_HEIGHT)
            return SXC_INTERVAL;
        if( height < SXC_KGW_HEIGHT)
            return SXC_INTERVAL_F1;
        if( height < SXC_KGW_FIX_HEIGHT)
            return SXC_INTERVAL_KGW;
        return SXC_INTERVAL_KGW_FIX;
    }


    /**
     * How much time in seconds is supposed to pass between "interval" blocks. If the actual elapsed time is
     * significantly different from this value, the network difficulty formula will produce a different value.
     *
     */
    public int getTargetTimespan(int height) {
        if ( height < SXC_FORK1_HEIGHT)
            return SXC_TARGET_TIMESPAN;
        if ( height < SXC_KGW_HEIGHT)
            return SXC_TARGET_TIMESPAN_F1;
        if ( height < SXC_KGW_FIX_HEIGHT)
            return SXC_TARGET_TIMESPAN_KGW;
        return 60;
    }

    public MonetaryFormat getMonetaryFormat() {
        return SXC;
    }

    @Override
    public Coin getMaxMoney() {
        // TODO: Change to be Doge compatible
        return MAX_MONEY;
    }

    @Override
    public Coin getMinNonDustOutput() {
        return Coin.COIN;
    }

    @Override
    public String getUriScheme() {
        return "sexcoin";
    }

    @Override
    public boolean hasMaxMoney() {
        return false;
    }

    @Override
    public void checkDifficultyTransitions(StoredBlock storedPrev, Block nextBlock, BlockStore blockStore)
            throws VerificationException, BlockStoreException {
        if(skipDiffChecks){
            return; // this sucks.
        }
        try {
            final long newTargetCompact = calculateNewDifficultyTarget(storedPrev, nextBlock, blockStore);
            final long receivedTargetCompact = nextBlock.getDifficultyTarget();

            if (newTargetCompact < receivedTargetCompact) // ?? shouldn't this be newTargetCompact < receivedTargetCompact
                throw new VerificationException("Network provided difficulty bits do not match what was calculated: " +
                        newTargetCompact + " vs " + receivedTargetCompact);
        } catch (CheckpointEncounteredException ex) {
            // Just have to take it on trust then
        }
    }

    public long calculateNewDifficultyTarget(StoredBlock storedPrev, Block nextBlock, BlockStore blockStore)
            throws CheckpointEncounteredException, BlockStoreException {
        if(storedPrev.getHeight() + 1 < SXC_FORK1_HEIGHT){
            return(calculateNewDifficultyTarget_V1(storedPrev, nextBlock, blockStore));
        } else if( storedPrev.getHeight() + 1 < SXC_KGW_HEIGHT){
            return(calculateNewDifficultyTarget_V1(storedPrev, nextBlock, blockStore));
        } else if ( storedPrev.getHeight() +1 < SXC_KGW_FIX_HEIGHT) {
            return (calculateNewDifficultyTarget_kgw(storedPrev, nextBlock, blockStore));
        } else if( storedPrev.getHeight() +1 < SXC_AGE_VERIF_HEIGHT){
            return ( calculateNewDifficultyTarget_kgw( storedPrev, nextBlock, blockStore));
        } else
            return calculateNewDifficultyTarget_kgw( storedPrev, nextBlock, blockStore);

    }
    /**
     * Get the difficulty target expected for the next block. This includes all
     * the weird cases for Litecoin such as testnet blocks which can be maximum
     * difficulty if the block interval is high enough.
     *
     * @throws CheckpointEncounteredException if a checkpoint is encountered while
     *                                        calculating difficulty target, and therefore no conclusive answer can
     *                                        be provided.
     */
    public long calculateNewDifficultyTarget_V1(StoredBlock storedPrev, Block nextBlock, BlockStore blockStore)
            throws VerificationException, BlockStoreException, CheckpointEncounteredException {
        final Block prev = storedPrev.getHeader();
        final int previousHeight = storedPrev.getHeight();
        final int retargetInterval = this.getInterval(previousHeight + 1);

        // Is this supposed to be a difficulty transition point?
        if ((storedPrev.getHeight() + 1) % retargetInterval != 0) {
            if (this.allowMinDifficultyBlocks()) {
                // Special difficulty rule for testnet:
                // If the new block's timestamp is more than 5 minutes
                // then allow mining of a min-difficulty block.
                if (nextBlock.getTimeSeconds() > prev.getTimeSeconds() + getTargetSpacing(previousHeight + 1 ) * 2) {
                    return Utils.encodeCompactBits(maxTarget);
                } else {
                    // Return the last non-special-min-difficulty-rules-block
                    StoredBlock cursor = storedPrev;

                    while (cursor.getHeight() % retargetInterval != 0
                            && cursor.getHeader().getDifficultyTarget() == Utils.encodeCompactBits(this.getMaxTarget())) {
                        StoredBlock prevCursor = cursor.getPrev(blockStore);
                        if (prevCursor == null) {
                            break;
                        }
                        cursor = prevCursor;
                    }

                    return cursor.getHeader().getDifficultyTarget();
                }
            }

            // No ... so check the difficulty didn't actually change.
            return prev.getDifficultyTarget();
        }

        // We need to find a block far back in the chain. It's OK that this is expensive because it only occurs every
        // two weeks after the initial block chain download.
        StoredBlock cursor = storedPrev;
        int goBack = retargetInterval - 1;

        // Litecoin: This fixes an issue where a 51% attack can change difficulty at will.
        // Go back the full period unless it's the first retarget after genesis.
        // Code based on original by Art Forz
        if (cursor.getHeight() + 1 != retargetInterval)
            goBack = retargetInterval;

        for (int i = 0; i < goBack; i++) {
            if (cursor == null) {
                // This should never happen. If it does, it means we are following an incorrect or busted chain.
                throw new VerificationException(
                        "Difficulty transition point but we did not find a way back to the genesis block.");
            }
            cursor = blockStore.get(cursor.getHeader().getPrevBlockHash());
        }

        //We used checkpoints...
        if (cursor == null) {
            log.debug("Difficulty transition: Hit checkpoint!");
            throw new CheckpointEncounteredException();
        }

        Block blockIntervalAgo = cursor.getHeader();
        return this.calculateNewDifficultyTargetInner(previousHeight, prev.getTimeSeconds(),
                prev.getDifficultyTarget(), blockIntervalAgo.getTimeSeconds(),
                nextBlock.getDifficultyTarget());
    }

    /**
     * Calculate the difficulty target expected for the next block after a normal
     * recalculation interval. Does not handle special cases such as testnet blocks
     * being setting the target to maximum for blocks after a long interval.
     *
     * @param previousHeight   height of the block immediately before the retarget.
     * @param prev             the block immediately before the retarget block.
     * @param nextBlock        the block the retarget happens at.
     * @param blockIntervalAgo The last retarget block.
     * @return New difficulty target as compact bytes.
     */
    protected long calculateNewDifficultyTargetInner(int previousHeight, final Block prev,
                                                     final Block nextBlock, final Block blockIntervalAgo) {
        return this.calculateNewDifficultyTargetInner(previousHeight, prev.getTimeSeconds(),
                prev.getDifficultyTarget(), blockIntervalAgo.getTimeSeconds(),
                nextBlock.getDifficultyTarget());
    }

    /**
     * @param previousHeight       Height of the block immediately previous to the one we're calculating difficulty of.
     * @param previousBlockTime    Time of the block immediately previous to the one we're calculating difficulty of.
     * @param lastDifficultyTarget Compact difficulty target of the last retarget block.
     * @param lastRetargetTime     Time of the last difficulty retarget.
     * @param nextDifficultyTarget The expected difficulty target of the next
     *                             block, used for determining precision of the result.
     * @return New difficulty target as compact bytes.
     */
    protected long calculateNewDifficultyTargetInner(int previousHeight, long previousBlockTime,
                                                     final long lastDifficultyTarget, final long lastRetargetTime,
                                                     final long nextDifficultyTarget) {
        int height = previousHeight + 1;
        final int retargetTimespan = this.getTargetTimespan(height);
        int actualTime = (int) (previousBlockTime - lastRetargetTime);
        final int minTimespan = retargetTimespan / 4;
        final int maxTimespan = retargetTimespan * 4;

        actualTime = Math.min(maxTimespan, Math.max(minTimespan, actualTime));

        BigInteger newTarget = Utils.decodeCompactBits(lastDifficultyTarget);
        newTarget = newTarget.multiply(BigInteger.valueOf(actualTime));
        newTarget = newTarget.divide(BigInteger.valueOf(retargetTimespan));

        if (newTarget.compareTo(this.getMaxTarget()) > 0) {
            log.info("Difficulty hit proof of work limit: {}", newTarget.toString(16));
            newTarget = this.getMaxTarget();
        }

        int accuracyBytes = (int) (nextDifficultyTarget >>> 24) - 3;

        // The calculated difficulty is to a higher precision than received, so reduce here.
        BigInteger mask = BigInteger.valueOf(0xFFFFFFL).shiftLeft(accuracyBytes * 8);
        newTarget = newTarget.and(mask);
        return Utils.encodeCompactBits(newTarget);
    }

    @Override
    public int getChainID() {
        return AUXPOW_CHAIN_ID;
    }

    /**
     * Whether this network has special rules to enable minimum difficulty blocks
     * after a long interval between two blocks (i.e. testnet).
     */
    public abstract boolean allowMinDifficultyBlocks();

    /**
     * Get the hash to use for a block.
     */
    @Override
    public Sha256Hash getBlockDifficultyHash(Block block) {
        return ((AltcoinBlock) block).getScryptHash();
    }

    @Override
    public AltcoinSerializer getSerializer(boolean parseRetain) {
        return new AltcoinSerializer(this, parseRetain);
    }

    @Override
    public int getProtocolVersionNum(final ProtocolVersion version) {
        switch (version) {
            case PONG:
            case BLOOM_FILTER:
                return version.getBitcoinProtocolVersion();
            case CURRENT:
                return SXC_PROTOCOL_VERSION_CURRENT;
            case MINIMUM:
            default:
                return SXC_PROTOCOL_VERSION_AUXPOW;
        }
    }

    @Override
    public boolean isAuxPoWBlockVersion(long version) {
        return version >= BLOCK_MIN_VERSION_AUXPOW
                && (version & BLOCK_VERSION_FLAG_AUXPOW) > 0;
    }

    /**
     * Get the target time between individual blocks. Sexcoin uses this in its
     * difficulty calculations, but most coins don't.
     *
     * @param height the block height to calculate at.
     * @return the target spacing in seconds.
     */
    protected int getTargetSpacing(int height) {
        if(height <= SXC_FORK1_HEIGHT){
            return SXC_TARGET_SPACING;
        } else if ( height > SXC_FORK1_HEIGHT && height < SXC_KGW_HEIGHT ){
            return SXC_TARGET_SPACING_F1;
        } else if ( height >= SXC_KGW_HEIGHT && height < SXC_KGW_FIX_HEIGHT ){
            return SXC_TARGET_SPACING_KGW;
        }
        return 1; // sexcoin: after KGW we retargeted every block
    }

    /**
     * Set up KGW params for Sexcoin.
     * KGW is proving to be very problematic. ATM our theory is that there are some rounding errors in the
     * calculated targets in java because of the use of doubles in the calculations. If this is not true, then
     * someone figured out at way to sneak low difficulty blocks onto the blockchain, which is also
     * problematic.
     */

    private long calculateNewDifficultyTarget_kgw(StoredBlock storedPrev, Block nextBlock, BlockStore blockStore)
            throws CheckpointEncounteredException, BlockStoreException {

        /**
         * Sexcoin switched to retargeting every block at the KGW fix. Don't do this when initially syncing
         * the blockchain, instead only check a block at two week intervals. Return the difficulty target
         * for the current block to insure that they will pass the POW checks that are called from the validation
         * methods. KGW is a very expensive routine.
         */
        if( (storedPrev.getHeight()+1 < 3192088/*SXC_AUXPOW_HEIGHT*/ ) && (storedPrev.getHeight()+1 % 20160 != 0) )
            return(nextBlock.getDifficultyTarget());

        // Normal checks
        long targetBlocksSpacingSeconds = SXC_TARGET_SPACING_KGW; // 60
        long TimeDaySeconds   = 60 * 60 * 24;
        double PastSecondsMin = TimeDaySeconds * 0.25;
        long PastSecondsMax   = TimeDaySeconds * 7;
        long PastBlocksMin = (long) (PastSecondsMin / targetBlocksSpacingSeconds);
        long PastBlocksMax = PastSecondsMax / targetBlocksSpacingSeconds;

        return KimotoGravityWell(storedPrev, nextBlock, targetBlocksSpacingSeconds, PastBlocksMin, PastBlocksMax, blockStore);
    }

    private long KimotoGravityWell(StoredBlock storedPrev, Block nextBlock, long TargetBlocksSpacingSeconds,
                                   long PastBlocksMin, long PastBlocksMax, BlockStore blockStore)
                 throws BlockStoreException, VerificationException, CheckpointEncounteredException {
        /* current difficulty formula, megacoin - kimoto gravity well */
        StoredBlock BlockLastSolved = storedPrev;
        StoredBlock BlockReading = storedPrev;
        Block BlockCreating = nextBlock;

        //BlockCreating = BlockCreating;
        long PastBlocksMass = 0;
        long PastRateActualSeconds = 0;
        long PastRateTargetSeconds = 0;
        double PastRateAdjustmentRatio = 1f;
        BigInteger PastDifficultyAverage = BigInteger.valueOf(0);
        BigInteger PastDifficultyAveragePrev = BigInteger.valueOf(0);

        double EventHorizonDeviation;
        double EventHorizonDeviationFast;
        double EventHorizonDeviationSlow;


        if (BlockLastSolved == null || BlockLastSolved.getHeight() == 0 || (long) BlockLastSolved.getHeight() < PastBlocksMin) {
            return Utils.encodeCompactBits(getMaxTarget());
        }

        int i = 0;
        long LatestBlockTime = BlockLastSolved.getHeader().getTimeSeconds();

        for (i = 1; BlockReading != null && BlockReading.getHeight() > 0; i++) {
            if (PastBlocksMax > 0 && i > PastBlocksMax) {
                break;
            }
            PastBlocksMass++;

            if (i == 1) {
                PastDifficultyAverage = BlockReading.getHeader().getDifficultyTargetAsInteger();
            } else {
                PastDifficultyAverage = ((BlockReading.getHeader().getDifficultyTargetAsInteger().subtract(PastDifficultyAveragePrev)).divide(BigInteger.valueOf(i)).add(PastDifficultyAveragePrev));
            }
            PastDifficultyAveragePrev = PastDifficultyAverage;


            if (LatestBlockTime < BlockReading.getHeader().getTimeSeconds()) {
                if(BlockReading.getHeight() > SXC_KGW_FIX_HEIGHT)
                    //eliminates the ability to go back in time
                    LatestBlockTime = BlockReading.getHeader().getTimeSeconds();
            }

            PastRateActualSeconds = BlockLastSolved.getHeader().getTimeSeconds() - BlockReading.getHeader().getTimeSeconds();
            PastRateTargetSeconds = TargetBlocksSpacingSeconds * PastBlocksMass;
            PastRateAdjustmentRatio = 1.0f;
            if (BlockReading.getHeight() > SXC_KGW_FIX_HEIGHT) {
                //this should slow down the upward difficulty change
                if (PastRateActualSeconds < 1) {
                    PastRateActualSeconds = 1;
                }
            } else {
                if (PastRateActualSeconds < 0) {
                    PastRateActualSeconds = 0;
                }
            }
            if (PastRateActualSeconds != 0 && PastRateTargetSeconds != 0) {
                PastRateAdjustmentRatio = (double) PastRateTargetSeconds / (double) PastRateActualSeconds;
            }
            EventHorizonDeviation = 1 + (0.7084 * java.lang.Math.pow((Double.valueOf(PastBlocksMass) / Double.valueOf(144)), -1.228));
            EventHorizonDeviationFast = EventHorizonDeviation;
            EventHorizonDeviationSlow = 1 / EventHorizonDeviation;

            if (PastBlocksMass >= PastBlocksMin) {
                if ((PastRateAdjustmentRatio <= EventHorizonDeviationSlow) || (PastRateAdjustmentRatio >= EventHorizonDeviationFast)) {
                    break;
                }
            }
            StoredBlock BlockReadingPrev = blockStore.get(BlockReading.getHeader().getPrevBlockHash());
            if (BlockReadingPrev == null) {
                //Since we are using the checkpoint system, there may not be enough blocks to do this diff adjust, so skip until we do
                //break;
                throw new CheckpointEncounteredException();
            }
            BlockReading = BlockReadingPrev;
        }

        BigInteger newDifficulty = PastDifficultyAverage;
        if (PastRateActualSeconds != 0 && PastRateTargetSeconds != 0) {
            newDifficulty = newDifficulty.multiply(BigInteger.valueOf(PastRateActualSeconds));
            newDifficulty = newDifficulty.divide(BigInteger.valueOf(PastRateTargetSeconds));
        }

        if (newDifficulty.compareTo(getMaxTarget()) > 0) {
            log.info("Difficulty hit proof of work limit: {}", newDifficulty.toString(16));
            newDifficulty = getMaxTarget();
        }

        long error = nextBlock.getDifficultyTarget() - Utils.encodeCompactBits(newDifficulty);
        if ( error < 5000 && error > 0  ){
            log.info( "Difficulty comparison appears to be rounding error: Block#: {}, error: {}, NEW:{}, BLOCK:{}",
                    storedPrev.getHeight() + 1,
                    error,
                    Utils.encodeCompactBits(newDifficulty),
                    nextBlock.getDifficultyTarget());
            return nextBlock.getDifficultyTarget();
        }

        //log.info("newDifficulty: {}, {}",Utils.encodeCompactBits(newDifficulty), newDifficulty);
        //log.info("nextBlockDifficulty: {}, {}",nextBlock.getDifficultyTarget(), nextBlock.getDifficultyTargetAsInteger());
        //log.info("error: {}", error);
        return Utils.encodeCompactBits(newDifficulty);

    }

    private static class CheckpointEncounteredException extends Exception {

        private CheckpointEncounteredException() {
        }
    }
}


