import apiClient from './apiClient';

export const recordingApi = {
  uploadChunk: async (submissionId: number, blob: Blob, durationMs: number) => {
    const form = new FormData();
    form.append('submissionId', String(submissionId));
    form.append('file', blob, `chunk_${Date.now()}.webm`);
    form.append('durationMs', String(durationMs));

    const res = await apiClient.post<{ data: number }>('/recordings/upload', form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data;
  },

  listBySubmission: async (submissionId: number) => {
    const res = await apiClient.get<{
      data: { id: number; chunkIndex: number; fileSize: number; durationMs: number }[];
    }>(`/recordings/submission/${submissionId}`);
    return res.data.data;
  },

  getStreamUrl: (recordingId: number) => {
    const baseUrl = apiClient.defaults.baseURL ?? '';
    return `${baseUrl}/recordings/${recordingId}/stream`;
  },
};
