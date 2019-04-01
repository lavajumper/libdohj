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

import org.bitcoinj.core.Sha256Hash;

import static com.google.common.base.Preconditions.checkState;


/**
 * Parameters for the main Sexcoin production network on which people trade
 * goods and services.
 */
public class SexcoinMainNetParams extends AbstractSexcoinParams {
    public static final int MAINNET_MAJORITY_WINDOW = 2000;
    public static final int MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED = 1900;
    public static final int MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE = 1500;
    protected static final int DIFFICULTY_CHANGE_TARGET = 480;

    public SexcoinMainNetParams() {
        super(DIFFICULTY_CHANGE_TARGET);
        dumpedPrivateKeyHeader = 190; //This is always addressHeader + 128
        addressHeader = 62;
        p2shHeader = 69;
        acceptableAddressCodes = new int[]{addressHeader, p2shHeader};
        port = 9560;
        packetMagic = 0xface6969;
        // Note that while BIP44 makes HD wallets chain-agnostic, for legacy
        // reasons we use a Doge-specific header for main net. At some point
        // we'll add independent headers for BIP32 legacy and BIP44.
        bip32HeaderPub = 0x0488b21e; //The 4 byte header that serializes in base58 to "dgub".
        bip32HeaderPriv = 0x0488ade4; //The 4 byte header that serializes in base58 to "dgpv".
        genesisBlock.setDifficultyTarget(0x1e7fffffL);
        genesisBlock.setTime(1369146359L);
        genesisBlock.setNonce(244086L);
        id = ID_SXC_MAINNET;
        subsidyDecreaseBlockCount = 600000;
        spendableCoinbaseDepth = 70;

        // Note this is an SHA256 hash, not a Scrypt hash. Scrypt hashes are only
        // used in difficulty calculations.
        String genesisHash = genesisBlock.getHashAsString();
        checkState(genesisHash.equals("f42b9553085a1af63d659d3907a42c3a0052bbfa2693d3acf990af85755f2279"),
                genesisHash);

        majorityEnforceBlockUpgrade = MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE;
        majorityRejectBlockOutdated = MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED;
        majorityWindow = MAINNET_MAJORITY_WINDOW;

        // This contains (at a minimum) the blocks which are not BIP30 compliant. BIP30 changed how duplicate
        // transactions are handled. Duplicated transactions could occur in the case where a coinbase had the same
        // extraNonce and the same outputs but appeared at different heights, and greatly complicated re-org handling.
        // Having these here simplifies block connection logic considerably.
        checkpoints.put(0, Sha256Hash.wrap("f42b9553085a1af63d659d3907a42c3a0052bbfa2693d3acf990af85755f2279"));
        checkpoints.put(42279, Sha256Hash.wrap("9c9e2335131bbe0eb944bc6e5c86f7007e5a8c759137695b2bc7a368c01b3954"));
        checkpoints.put(42400, Sha256Hash.wrap("c1bf6bef89fb4a22245c55186b873a69f90ba52b459015a15f0d62ef28ae906e"));
        checkpoints.put(104679, Sha256Hash.wrap("e09f32ab8ed0d75e74ad2c1b25ff5c99f4bebb3d18e36737a5588b9a02ed248d"));
        checkpoints.put(128370, Sha256Hash.wrap("c18f91167ab1772610518b127343a8b8f8e1f0e94e0a8d3a1d87edba99b980f7"));
        checkpoints.put(144999, Sha256Hash.wrap("46ca2dab2bce42478ffccd02460767c4f88ea78f4cf71a70d91a1580b4c287cf"));
        checkpoints.put(165393, Sha256Hash.wrap("73e80b94f918e719be6951408e7e390a07967040f88c7e2a52a056d9ee0d78e5"));


        dnsSeeds = new String[]{
                "dnsseed.sexcoin.info",
                "dnsseed.lavajumper.com",
                "dnsseed2.sexcoin.info",
                "dnsseed3.sexcoin.info"
        };
    }

    private static SexcoinMainNetParams instance;

    public static synchronized SexcoinMainNetParams get() {
        if (instance == null) {
            instance = new SexcoinMainNetParams();
        }
        return instance;
    }

    @Override
    public boolean allowMinDifficultyBlocks() {
        return false;
    }

    @Override
    public String getPaymentProtocolId() {
        // TODO: CHANGE THIS
        return PAYMENT_PROTOCOL_ID_MAINNET;
    }

    @Override
    public boolean isTestNet() {
        return false;
    }
}
