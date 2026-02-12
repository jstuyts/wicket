import { h, render } from './preact-10.28.3.module.js';

function createNode(element) {
    const props = {};
    for (let i = 0; i < element.attributes.length; i++) {
        const attribute = element.attributes[i];
        props[attribute.name] = attribute.value;
    }

    const children = [];
    if (element.childNodes.length > 0) {
        for (let i = 0; i < element.childNodes.length; i++) {
            const child = element.childNodes[i];
            if (child.nodeType === Node.ELEMENT_NODE) {
                children.push(createNode(child));
            } else if (child.nodeType === Node.TEXT_NODE) {
                children.push(child.textContent);
            }
        }
    }

    return h(element.tagName.toLowerCase(), props, children);
}

function createVirtualDOM(htmlString) {
    const parser = new DOMParser();
    const doc = parser.parseFromString(htmlString, "text/html");
    const elementNode = doc.body.firstElementChild;

    return createNode(elementNode);
}

Wicket.DOM.registerReplacementType("preact", function (element, text) {
    if (element.parentElement.childElementCount === 1) {
        render(createVirtualDOM(text), element.parentElement);
    } else {
        console.error("Preact replacement: element with ID: " + element.id + ", is not the only element in its parent.");
    }
});
