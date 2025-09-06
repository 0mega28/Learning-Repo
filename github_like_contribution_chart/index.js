const contributions = document.getElementById("contributions");

const rows = 7;
const columns = 52;

function getRandomContribution() {
    const randomNumber = Math.floor(Math.random() * 100);

    let contributionSize;
    if (randomNumber < 40) {
        contributionSize = "no";
    } else if (randomNumber < 65) {
        contributionSize = "low";
    } else if (randomNumber < 85) {
        contributionSize = "mid";
    } else {
        contributionSize = "high";
    }

    return `${contributionSize}-contribution`;
}

const fragment = document.createDocumentFragment();
Array(rows * columns)
    .fill(0)
    .map(_ => { 
        const box = document.createElement('div'); 
        box.classList.add('box');
        box.classList.add(getRandomContribution());
        return box;
    })
    .forEach(box => fragment.appendChild(box))

contributions.append(fragment);