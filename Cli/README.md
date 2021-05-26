Cli application that displays messages about accounts as they arrive to the pubsub component of the Tracking System.

#### Run
    docker run -i --network=tracking-system-network trackingsystem_cli1

Use -f flag to display only messages regarding certain accounts. You should specify ids of accounts.

    docker run -i --network=tracking-system-network trackingsystem_cli1 -f a0100000xc3f a0100000xc4f

Use -h flag for help.

    docker run -i --network=tracking-system-network trackingsystem_cli1 -h

