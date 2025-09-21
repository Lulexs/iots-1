import "@mantine/core/styles.css";

import { MantineProvider } from "@mantine/core";
import UnexpectedEventsViewer from "./UnexpectedEventsViewer";

function App() {
  return (
    <MantineProvider>
      <UnexpectedEventsViewer />
    </MantineProvider>
  );
}

export default App;
