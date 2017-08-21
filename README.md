### nifi-http2

This is a simple test that issues POST requests to a local endpoint using HTTP and HTTP2 and measures the relative performance with varying message sizes.

The following trials are executed:

- HTTP
  - 100 10B requests
  - 100 100B requests
  - 100 1KB requests
  - 100 1MB requests
  - 100 100MB requests

- HTTP2
  - 100 10B requests
  - 100 100B requests
  - 100 1KB requests
  - 100 1MB requests
  - 100 100MB requests
  
  Results are shared [here](https://drive.google.com/file/d/0B5R61h86AIIOdTdXTG9QS1B3cWM/view?usp=sharing)
