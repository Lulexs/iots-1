import "@mantine/core/styles.css";

import { MantineProvider } from "@mantine/core";
import UnexpectedEventsViewer from "./UnexpectedEventsViewer";
import PredictionEventsViewer from "./PredictionEventsViewer";

function App() {
  return (
    <MantineProvider>
      <UnexpectedEventsViewer />
      <PredictionEventsViewer />
    </MantineProvider>
  );
}

export default App;
