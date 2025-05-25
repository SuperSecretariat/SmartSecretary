const express = require('express');
const multer = require('multer');
const cors = require('cors');
const { exec } = require('child_process');
const fs = require('fs');
const path = require('path');

const app = express();
app.use(cors());
const upload = multer({ dest: 'uploads/' });

app.post('/transcribe', upload.single('audio'), (req, res) => {
  console.log('Fișier primit:', req.file);
  const audioPath = req.file.path;
  const wavPath = audioPath + '.wav';

  // 1. Convertește .webm la .wav cu ffmpeg
  console.log('Pornesc conversia ffmpeg:', audioPath, '->', wavPath);
  exec(`ffmpeg -y -i "${audioPath}" "${wavPath}"`, (err, stdout, stderr) => {
    console.log('Am terminat ffmpeg');
    if (err) {
      console.error('Eroare ffmpeg:', stderr); 
      fs.unlinkSync(audioPath);
      return res.status(500).send('Eroare la conversie audio');
    }
    // 2. Rulează transcrierea pe fișierul .wav
    const pythonPath = process.env.PYTHON_PATH || 'python'; // Aici trebuie calea de la Python 3.11
    exec(`${pythonPath} ../transcriere.py "${wavPath}"`, (error, stdout, stderr) => {
      fs.unlinkSync(audioPath);
      fs.unlinkSync(wavPath);
      if (error) {
        console.error('Eroare transcriere:', stderr); // <-- vezi detaliile erorii
        res.status(500).send('Eroare la transcriere');
      } else {
        res.send(stdout);
      }
    });
  });
});

app.listen(5000, () => console.log('Serverul rulează pe portul 5000'));