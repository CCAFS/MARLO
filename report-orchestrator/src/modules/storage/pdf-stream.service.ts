export async function fetchPdfBuffer(pdfUrl: string): Promise<Buffer> {
  const response = await fetch(pdfUrl, { method: 'GET' });
  if (!response.ok) {
    throw new Error(`Failed to download PDF (${response.status}): ${pdfUrl}`);
  }

  const arrayBuffer = await response.arrayBuffer();
  return Buffer.from(arrayBuffer);
}
