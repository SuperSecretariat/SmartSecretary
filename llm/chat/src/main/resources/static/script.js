async function sendMessage() {
    const inputField = document.getElementById('user-input');
    const message = inputField.value.trim();
    const chatBox = document.getElementById('chat-box');

    if (!message) return;

    chatBox.innerHTML += `<div class="message user"><strong>Tu:</strong> ${message}</div>`;
    inputField.value = '';
    chatBox.scrollTop = chatBox.scrollHeight;

    try {
        const response = await fetch('https://4c11-86-125-182-73.ngrok-free.app/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message: message })
        });

        const data = await response.json();

        chatBox.innerHTML += `<div class="message bot"><strong>Model:</strong> ${data.answer}</div>`;

        if (data.sources && data.sources.length > 0) {
            chatBox.innerHTML += `<div class="message bot"><strong>Surse:</strong> ${data.sources.join('\n')}</div>`;
        }

        if (data.chunks && data.chunks.length > 0) {
            chatBox.innerHTML += `<div class="message bot"><strong>Fragmente:</strong></div>`;
            data.chunks.forEach((chunk, index) => {
                chatBox.innerHTML += `<div class="message bot">(${index + 1}) ${chunk}</div>`;
            });
        }

        chatBox.scrollTop = chatBox.scrollHeight;
    } catch (error) {
        chatBox.innerHTML += `<div class="message bot"><strong>Model:</strong> Eroare la comunicare cu serverul.</div>`;
    }
}

window.onload = () => {
    const inputField = document.getElementById('user-input');
    inputField.focus();

    inputField.addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            sendMessage();
        }
    });
};
