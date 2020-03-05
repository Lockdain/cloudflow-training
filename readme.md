### Lighbend Cloudflow Demo Project

This is a simple project that demonstrates a very basic
functionality of Lightbend Cloudflow framework.
It can be used as a basis for the further experiments 
and discovery.

##### How to build

A simplest way to build the project is to open it 
in Intellij IDEA, proceed to the `sbt-shell` window and 
run the `runLocal` sbt task.

The project will be then built and compiled. If everything 
is as expected you will see the following message in your
`sbt-shell`:

`Running cloudflow-pipeline
To terminate, press [ENTER]`

From this point your pipeline is ready to use.

##### How to test

Pay attention to the `sbt-shell` output. Under the `Streamlets`
group you will see an HTTP port number being bounded
to the `payment-ingress` streamlet.

The streamlet is an entry point for the whole pipeline.
Go to the `test-data` folder and open the `send-payments.sh`.
The script reads JSON strings from the `input-payments.json`
and sends them to the `payment-ingress` streamlet. You
probably need to define the right HTTP port number obtained
from `sbt-shell` log.

##### The business case

The use case scenario is pretty simple. The whole application
simulates a kind of real-time fraud detection. Please look at
`pipeline-schema.png` under the root folder for the basic streamlets representation.

#### payment-http-ingress

Acts as a source for the whole pipeline. Receives and
handles incoming requests as an instances of `Payment`.

#### payment-validator

Receives entities of `Payment`. Sends invalid ones 
to the `payment-invalid-logger` streamlet (`amount` <= 0 or 
`countryTo` = "Tunisia")

Valid records will be sent to `payment-windowing-flink` streamlet.

#### payment-windowing-flink

The streamlet performs some basic aggregations on the sliding
window. The idea is as simple as if somebody executes transactions
within window with a total amount more that `50 000` than we
consider such payments as a fraud and remap them to `EnrichedPayment`
entity.

#### payment-collector-spark

Simply receives `EnrichedPayment` stream from `payment-windowing-flink`
and log them to the console without any additional logic
applied.
