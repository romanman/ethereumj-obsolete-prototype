package org.ethereum.vm;

import org.ethereum.core.Block;
import org.ethereum.core.Transaction;
import org.ethereum.db.Repository;
import org.ethereum.manager.WorldManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;

/**
 * www.ethereumJ.com
 *
 * @author: Roman Mandeleil
 * Created on: 08/06/2014 09:59
 */
public class ProgramInvokeFactory {

    private static Logger logger = LoggerFactory.getLogger("VM");

        // Invocation by the wire tx
    public static ProgramInvoke createProgramInvoke(Transaction tx, Block block, Repository repository) {

        // https://ethereum.etherpad.mozilla.org/26
        Block lastBlock = WorldManager.getInstance().getBlockchain().getLastBlock();

        /***         ADDRESS op       ***/
        // YP: Get address of currently executing account.
        byte[] address  =  (tx.isContractCreation())? tx.getContractAddress(): tx.getReceiveAddress();

        /***         ORIGIN op       ***/
        // YP: This is the sender of original transaction; it is never a contract.
        byte[] origin  = tx.getSender();

        /***         CALLER op       ***/
        // YP: This is the address of the account that is directly responsible for this execution.
        byte[] caller = tx.getSender();

        /***         BALANCE op       ***/
        byte[] balance = repository.getBalance(address).toByteArray();

        /***         GASPRICE op       ***/
        byte[] gasPrice = tx.getGasPrice();

        /*** GAS op ***/
        byte[] gas = tx.getGasLimit();

        /***        CALLVALUE op      ***/
        byte[] callValue = tx.getValue() == null ? new byte[]{0} : tx.getValue();

        /***     CALLDATALOAD  op   ***/
        /***     CALLDATACOPY  op   ***/
        /***     CALLDATASIZE  op   ***/
        byte[] data = tx.getData();
        if (data == null) data = new byte[]{};

        /***    PREVHASH  op  ***/
        byte[] lastHash = lastBlock.getHash();

        /***   COINBASE  op ***/
        byte[] coinbase = block.getCoinbase();

        /*** TIMESTAMP  op  ***/
        long timestamp = block.getTimestamp();

        /*** NUMBER  op  ***/
        long number = block.getNumber();

        /*** DIFFICULTY  op  ***/
        byte[] difficulty = block.getDifficulty();

        /*** GASLIMIT op ***/
        long gaslimit = block.getGasLimit();

        if (logger.isInfoEnabled()) {
            logger.info("Top level call: \n" +
                    "address={}\n" +
                    "origin={}\n"  +
                    "caller={}\n"  +
                    "balance={}\n" +
                    "gasPrice={}\n" +
                    "gas={}\n" +
                    "callValue={}\n" +
                    "data={}\n" +
                    "lastHash={}\n" +
                    "coinbase={}\n" +
                    "timestamp={}\n" +
                    "blockNumber={}\n" +
                    "difficulty={}\n" +
                    "gaslimit={}\n",

                    Hex.toHexString(address),
                    Hex.toHexString(origin),
                    Hex.toHexString(caller),
                    new BigInteger(balance).longValue(),
                    new BigInteger(gasPrice).longValue(),
                    new BigInteger(gas).longValue(),
                    new BigInteger(callValue).longValue(),
                    Hex.toHexString(data),
                    Hex.toHexString(lastHash),
                    Hex.toHexString(coinbase),
                    timestamp,
                    number,
                    Hex.toHexString(difficulty),
                    gaslimit);
        }

        ProgramInvoke programInvoke =
            new ProgramInvokeImpl(address, origin, caller, balance, gasPrice, gas, callValue, data,
                    lastHash,  coinbase,  timestamp,  number,  difficulty,  gaslimit,
                    repository);

        return programInvoke;
    }


    /**
     * This invocation created for contract call contract
     */
    public static ProgramInvoke createProgramInvoke(Program program, DataWord toAddress,
                                                    DataWord inValue, DataWord inGas,
                                                    BigInteger balanceInt,  byte[] dataIn,
                                                    Repository repository, int callDeep) {

        DataWord address = toAddress;
        DataWord origin = program.getOriginAddress();
        DataWord caller = program.getOwnerAddress();

        DataWord balance = new DataWord(balanceInt.toByteArray());
        DataWord gasPrice = program.getGasPrice();
        DataWord gas = inGas;
        DataWord callValue = inValue;

        byte[] data = dataIn;
        DataWord lastHash =  program.getPrevHash();
        DataWord coinbase =  program.getCoinbase();
        DataWord timestamp = program.getTimestamp();
        DataWord number =  program.getNumber();
        DataWord difficulty = program.getDifficulty();
        DataWord gasLimit = program.getGaslimit();

        if (logger.isInfoEnabled()) {


            logger.info("Internal call: \n" +
                            "address={}\n" +
                            "origin={}\n"  +
                            "caller={}\n"  +
                            "balance={}\n" +
                            "gasPrice={}\n" +
                            "gas={}\n" +
                            "callValue={}\n" +
                            "data={}\n" +
                            "lastHash={}\n" +
                            "coinbase={}\n" +
                            "timestamp={}\n" +
                            "blockNumber={}\n" +
                            "difficulty={}\n" +
                            "gaslimit={}\n",
                    Hex.toHexString(address.getLast20Bytes()),
                    Hex.toHexString(origin.getLast20Bytes()),
                    Hex.toHexString(caller.getLast20Bytes()),
                    balance.longValue(),
                    gasPrice.longValue(),
                    gas.longValue(),
                    callValue.longValue(),
                    data == null ? "null": Hex.toHexString(data),
                    Hex.toHexString(lastHash.getData()),
                    Hex.toHexString(coinbase.getLast20Bytes()),
                    timestamp.longValue(),
                    number.longValue(),
                    Hex.toHexString(difficulty.getNoLeadZeroesData()),
                    gasLimit.longValue());
        }

        return new ProgramInvokeImpl(address, origin, caller, balance, gasPrice, gas, callValue,
                data, lastHash, coinbase, timestamp, number, difficulty, gasLimit,
                repository, callDeep);
    }
}
