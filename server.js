import express from 'express';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());

// API health endpoint
app.get('/api/health', (req, res) => {
  res.json({
    status: 'ok',
    app: 'Link Push',
    version: '2.1.1',
    author: 'ZaidHB'
  });
});

// Serve static assets from project root
app.use(express.static(__dirname));

// Fallback all non-API GET requests to index.html
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'index.html'));
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Link Push Web Server running at http://0.0.0.0:${PORT}`);
});
