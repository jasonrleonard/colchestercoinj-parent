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

package com.google.colchestercoin.params;

import com.google.colchestercoin.core.NetworkParameters;
import com.google.colchestercoin.core.Sha256Hash;
import com.google.colchestercoin.core.Utils;
import org.spongycastle.util.encoders.Hex;

import static com.google.common.base.Preconditions.checkState;

/**
 * Parameters for the testnet, a separate public instance of Bitcoin that has relaxed rules suitable for development
 * and testing of applications and new Bitcoin versions.
 */
public class TestNet3Params extends NetworkParameters {
    public TestNet3Params() {
        super();
        id = ID_TESTNET;
        packetMagic = 0xfbc0b6db;
        port = 29333;
        addressHeader = 28;
        p2shHeader = 22;
        acceptableAddressCodes = new int[] { addressHeader, p2shHeader };
        interval = INTERVAL;
        targetTimespan = TARGET_TIMESPAN;
        proofOfWorkLimit = Utils.decodeCompactBits(0x1d0fffffL);
        dumpedPrivateKeyHeader = 156;
        genesisBlock.setTime(1460409903L);
        genesisBlock.setDifficultyTarget(0x1e0ffff0);
        genesisBlock.setNonce(106749L);
        spendableCoinbaseDepth = 100;
        subsidyDecreaseBlockCount = 840000;
        genesisBlock.setMerkleRoot(new Sha256Hash("0072849199923b05d61ffb0814527b730778c5a479fa869581316c187e6ec1da"));
        String genesisHash = genesisBlock.getHashAsString();
        LOGGER.info("Genesis Hash: " + genesisHash.toString());
        dnsSeeds = null;
    }

    private static TestNet3Params instance;
    public static synchronized TestNet3Params get() {
        if (instance == null) {
            instance = new TestNet3Params();
        }
        return instance;
    }
}
