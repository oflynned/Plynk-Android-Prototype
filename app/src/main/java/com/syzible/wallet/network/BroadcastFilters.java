package com.syzible.wallet.network;

/**
 * Created by ed on 20/11/2017.
 */

public enum BroadcastFilters {
    on_new_message_received {
        @Override
        public String toString() {
            return "com.syzible.plynk." + on_new_message_received.name();
        }
    },

    on_money_transfer_received {
        @Override
        public String toString() {
            return "com.syzible.plynk." + on_money_transfer_received.name();
        }
    },

    on_money_transfer_sent {
        @Override
        public String toString() {
            return "com.syzible.plynk." + on_money_transfer_sent.name();
        }
    }
}
