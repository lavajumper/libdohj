/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.libdohj.params;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;

import java.math.BigInteger;

import static com.google.common.base.Preconditions.checkState;

/**
 * Network parameters for the regression test mode of sexcoind in which all blocks are trivially solvable.
 */
public class SexcoinRegTestParams extends SexcoinTestNet3Params {
    private static final BigInteger MAX_TARGET = new BigInteger("7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);
    public static final int REGTEST_MAJORITY_REJECT_BLOCK_OUTDATED = 950;
    public static final int REGTEST_MAJORITY_ENFORCE_BLOCK_UPGRADE = 750;

    public SexcoinRegTestParams() {
        super();
        // Difficulty adjustments are disabled for regtest.
        // By setting the block interval for difficulty adjustments to Integer.MAX_VALUE we make sure difficulty never changes.
        interval = Integer.MAX_VALUE;
        maxTarget = MAX_TARGET;
        subsidyDecreaseBlockCount = 400;
        port = 29560;
        id = ID_SXC_REGTEST;
        packetMagic = 0xface9696;

        majorityEnforceBlockUpgrade = REGTEST_MAJORITY_ENFORCE_BLOCK_UPGRADE;
        majorityRejectBlockOutdated = REGTEST_MAJORITY_REJECT_BLOCK_OUTDATED;

        genesisBlock.setTime(1296688602L);
        genesisBlock.setDifficultyTarget(0x207fffff);
        genesisBlock.setNonce(2);
        checkState(genesisBlock.getHashAsString().toLowerCase().equals("3d2160a3b5dc4a9d62e7e66a295f70313ac808440ef7400d6c0772171ce973a5"));
    }

    @Override
    public boolean allowEmptyPeerChain() {
        return true;
    }

    private static Block genesis;

    /*
    @Override
    public Block getGenesisBlock() {
        synchronized (SexcoinRegTestParams.class) {
            if (genesis == null) {
                genesis = super.getGenesisBlock();
                genesis.setNonce(2);
                genesis.setDifficultyTarget(0x207fffffL);
                genesis.setTime(1296688602L);
                checkState(genesis.getVersion() == 1);
                checkState(genesis.getHashAsString().toLowerCase().equals("3d2160a3b5dc4a9d62e7e66a295f70313ac808440ef7400d6c0772171ce973a5"));
                genesis.verifyHeader();
            }
            return genesis;
        }
    }
    */

    private static SexcoinRegTestParams instance;

    public static synchronized SexcoinRegTestParams get() {
        if (instance == null) {
            instance = new SexcoinRegTestParams();
        }
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return ID_SXC_REGTEST;
    }

    @Override
    /** the testnet rules don't work for regtest, where difficulty stays the same */
    public long calculateNewDifficultyTarget(StoredBlock storedPrev, Block nextBlock, BlockStore blockStore)
            throws VerificationException, BlockStoreException {
        final Block prev = storedPrev.getHeader();
        return prev.getDifficultyTarget();
    }

    @Override
    public boolean allowMinDifficultyBlocks() {
        return false;
    }

    @Override
    public boolean isTestNet() {
        return false;
    }
}
